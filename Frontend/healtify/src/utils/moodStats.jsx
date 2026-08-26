import { WEEKDAY_NAMES, formatDateKey } from './calendarUtils';

/** Zakresy przełącznika nad wykresem (w dniach). */
export const RANGE_OPTIONS = [7, 30, 90];

/** Dzień, którego dotyczy wpis - liczy się data, nie godzina. */
const dayKey = (entry) => entry.entryAt.slice(0, 10);

const avgOf = (numbers) => (numbers.length ? numbers.reduce((a, b) => a + b, 0) / numbers.length : null);

/** Klucz YYYY-MM-DD dla dnia sprzed `back` dni. */
export const shiftDayKey = (back) => {
    const date = new Date();
    date.setDate(date.getDate() - back);
    return formatDateKey(date);
};

/** Wpisy z ostatnich `days` dni. Daty z przyszłości pomijamy - analiza patrzy wstecz. */
export const entriesInRange = (entries, days) => {
    const from = shiftDayKey(days - 1);
    const to = shiftDayKey(0);
    return entries.filter((entry) => dayKey(entry) >= from && dayKey(entry) <= to);
};

/** Jeden punkt na dzień - kilka wpisów z tego samego dnia uśredniamy. */
export const dailyAverages = (entries) => {
    const byDay = new Map();
    entries.forEach((entry) => {
        const key = dayKey(entry);
        byDay.set(key, [...(byDay.get(key) || []), entry.moodScale]);
    });
    return [...byDay.entries()]
        .map(([key, scores]) => ({ key, avg: avgOf(scores), count: scores.length }))
        .sort((a, b) => a.key.localeCompare(b.key));
};

/** Średnia z ostatnich 7 dni zestawiona z 7 dniami przed nimi - stąd strzałka trendu. */
export const weekComparison = (entries) => {
    const current = entriesInRange(entries, 7);
    const from = shiftDayKey(13);
    const to = shiftDayKey(7);
    const previous = entries.filter((entry) => dayKey(entry) >= from && dayKey(entry) <= to);

    const currentAvg = avgOf(current.map((entry) => entry.moodScale));
    const previousAvg = avgOf(previous.map((entry) => entry.moodScale));
    return {
        current: currentAvg,
        previous: previousAvg,
        delta: currentAvg !== null && previousAvg !== null ? currentAvg - previousAvg : null,
        count: current.length
    };
};

/** Ile razy padła każda ocena 1-5. */
export const moodDistribution = (entries) =>
    [5, 4, 3, 2, 1].map((score) => ({
        score,
        count: entries.filter((entry) => entry.moodScale === score).length
    }));

/** Średnia dla każdego dnia tygodnia - pokazuje, które dni wypadają gorzej. */
export const weekdayAverages = (entries) => {
    const buckets = WEEKDAY_NAMES.map(() => []);
    entries.forEach((entry) => {
        // getDay(): 0 = niedziela, więc przesuwamy tak, żeby poniedziałek był pierwszy.
        const weekday = (new Date(entry.entryAt).getDay() + 6) % 7;
        buckets[weekday].push(entry.moodScale);
    });
    return WEEKDAY_NAMES.map((label, index) => ({
        label,
        avg: avgOf(buckets[index]),
        count: buckets[index].length
    }));
};

/** Najczęstsze objawy i nastrój w dniach z nimi na tle pozostałych wpisów. */
export const symptomStats = (entries, limit = 5) => {
    const counts = new Map();
    entries.forEach((entry) => {
        (entry.symptoms || []).forEach((symptom) => counts.set(symptom, (counts.get(symptom) || 0) + 1));
    });

    const has = (entry, symptom) => (entry.symptoms || []).includes(symptom);
    return [...counts.entries()]
        .sort((a, b) => b[1] - a[1] || a[0].localeCompare(b[0]))
        .slice(0, limit)
        .map(([name, count]) => ({
            name,
            count,
            avgWith: avgOf(entries.filter((entry) => has(entry, name)).map((entry) => entry.moodScale)),
            avgWithout: avgOf(entries.filter((entry) => !has(entry, name)).map((entry) => entry.moodScale))
        }));
};

/** Ile dni z rzędu ma wpis. Liczymy od dziś, a jak dzisiejszego jeszcze nie ma - od wczoraj. */
export const entryStreak = (entries) => {
    const days = new Set(entries.map(dayKey));
    let start = 0;
    if (!days.has(shiftDayKey(0))) {
        if (!days.has(shiftDayKey(1))) {
            return 0;
        }
        start = 1;
    }
    let streak = 0;
    while (days.has(shiftDayKey(start + streak))) {
        streak++;
    }
    return streak;
};

/** Dzień z najwyższą i najniższą średnią w okresie. */
export const bestAndWorstDay = (days) => {
    if (days.length === 0) {
        return { best: null, worst: null };
    }
    const sorted = [...days].sort((a, b) => a.avg - b.avg);
    return { best: sorted[sorted.length - 1], worst: sorted[0] };
};
