import React, { useMemo, useState } from 'react';
import '../css/mood.css';
import { moodClass } from '../utils/calendarUtils';
import {
    RANGE_OPTIONS,
    bestAndWorstDay,
    dailyAverages,
    entriesInRange,
    entryStreak,
    moodDistribution,
    shiftDayKey,
    symptomStats,
    weekComparison,
    weekdayAverages
} from '../utils/moodStats';

// Układ wykresu w jednostkach viewBox - SVG skaluje się potem do szerokości karty.
const CHART = { width: 640, height: 200, left: 30, right: 12, top: 12, bottom: 26 };
const PLOT_WIDTH = CHART.width - CHART.left - CHART.right;
const PLOT_HEIGHT = CHART.height - CHART.top - CHART.bottom;

const DEFAULT_RANGE = 30;
const TOP_SYMPTOMS = 5;

const formatMood = (value) => (value === null ? '—' : value.toFixed(1).replace('.', ','));

/** "20.08" - etykiety na osi i w kafelkach. */
const formatDayLabel = (key) => `${key.slice(8, 10)}.${key.slice(5, 7)}`;

const daysBetween = (fromKey, toKey) => {
    const toUtc = (key) => Date.UTC(Number(key.slice(0, 4)), Number(key.slice(5, 7)) - 1, Number(key.slice(8, 10)));
    return Math.round((toUtc(toKey) - toUtc(fromKey)) / 86400000);
};

const plural = (count, one, few, many) => {
    if (count === 1) return one;
    const rest = count % 10;
    const teens = count % 100;
    return rest >= 2 && rest <= 4 && (teens < 12 || teens > 14) ? few : many;
};

function MoodAnalysis({ entries }) {
    const [range, setRange] = useState(DEFAULT_RANGE);
    const allEntries = useMemo(() => entries || [], [entries]);

    const stats = useMemo(() => {
        const ranged = entriesInRange(allEntries, range);
        const days = dailyAverages(ranged);
        return {
            ranged,
            days,
            average: days.length ? ranged.reduce((sum, entry) => sum + entry.moodScale, 0) / ranged.length : null,
            week: weekComparison(allEntries),
            streak: entryStreak(allEntries),
            distribution: moodDistribution(ranged),
            weekdays: weekdayAverages(ranged),
            symptoms: symptomStats(ranged, TOP_SYMPTOMS),
            ...bestAndWorstDay(days)
        };
    }, [allEntries, range]);

    const { ranged, days, average, week, streak, distribution, weekdays, symptoms, best, worst } = stats;

    // Oś X liczy się od pierwszego dnia zakresu, więc przerwy między wpisami są widoczne.
    const startKey = shiftDayKey(range - 1);
    const xFor = (key) => CHART.left + (daysBetween(startKey, key) / (range - 1)) * PLOT_WIDTH;
    const yFor = (mood) => CHART.top + ((5 - mood) / 4) * PLOT_HEIGHT;

    const points = days.map((day) => ({ ...day, x: xFor(day.key), y: yFor(day.avg) }));

    // Kilka równo rozłożonych dat pod wykresem - przy 90 dniach nie zmieści się ich więcej.
    const labelStep = Math.ceil((range - 1) / 5);
    const axisLabels = [];
    for (let offset = 0; offset < range; offset += labelStep) {
        axisLabels.push(shiftDayKey(range - 1 - offset));
    }

    const maxCount = Math.max(...distribution.map((slot) => slot.count), 1);

    const summary = () => {
        if (ranged.length === 0) {
            return `Brak wpisów z ostatnich ${range} dni - dodaj wpis w dzienniku, żeby zobaczyć analizę.`;
        }
        const base = `Ostatnie ${range} dni: średnia ${formatMood(average)}/5 z ${ranged.length} ${plural(ranged.length, 'wpisu', 'wpisów', 'wpisów')}.`;
        if (week.current === null) {
            return `${base} W tym tygodniu nie ma jeszcze żadnego wpisu.`;
        }
        if (week.delta === null) {
            return `${base} W tym tygodniu ${formatMood(week.current)}/5.`;
        }
        if (Math.abs(week.delta) < 0.1) {
            return `${base} W tym tygodniu ${formatMood(week.current)}/5 - tyle samo co w poprzednim.`;
        }
        const direction = week.delta > 0 ? 'lepiej' : 'gorzej';
        return `${base} W tym tygodniu ${formatMood(week.current)}/5, czyli o ${formatMood(Math.abs(week.delta))} ${direction} niż w poprzednim.`;
    };

    if (allEntries.length === 0) {
        return (
            <div className="datablock doctor-panel mood-panel">
                <p>Brak wpisów w dzienniku - analiza pojawi się po pierwszym wpisie.</p>
            </div>
        );
    }

    return (
        <div className="datablock doctor-panel mood-panel">
            <div className="mood-header">
                <p className="mood-summary">{summary()}</p>
                <div className="mood-range">
                    {RANGE_OPTIONS.map((option) => (
                        <button
                            type="button"
                            key={option}
                            className={option === range ? 'tag-chip active' : 'tag-chip'}
                            onClick={() => setRange(option)}
                        >
                            {option} dni
                        </button>
                    ))}
                </div>
            </div>

            <div className="mood-stats">
                <div className="doctor-stat">
                    <span className="doctor-stat-value">{formatMood(week.current)}</span>
                    <span className="doctor-stat-label">średnia w tygodniu</span>
                </div>
                <div className="doctor-stat">
                    <span className={`doctor-stat-value ${week.delta > 0 ? 'mood-up' : ''} ${week.delta < 0 ? 'mood-down' : ''}`}>
                        {week.delta === null ? '—' : `${week.delta > 0 ? '▲' : '▼'} ${formatMood(Math.abs(week.delta))}`}
                    </span>
                    <span className="doctor-stat-label">zmiana vs poprzedni tydzień</span>
                </div>
                <div className="doctor-stat">
                    <span className="doctor-stat-value">{ranged.length}</span>
                    <span className="doctor-stat-label">{plural(ranged.length, 'wpis', 'wpisy', 'wpisów')} w okresie</span>
                </div>
                <div className="doctor-stat">
                    <span className="doctor-stat-value">{streak}</span>
                    <span className="doctor-stat-label">{plural(streak, 'dzień z rzędu', 'dni z rzędu', 'dni z rzędu')}</span>
                </div>
            </div>

            {points.length === 0 ? (
                <p className="mood-empty">Brak danych do wykresu w tym zakresie.</p>
            ) : (
                <svg className="mood-chart" viewBox={`0 0 ${CHART.width} ${CHART.height}`} role="img" aria-label="Wykres nastroju w czasie">
                    {[1, 2, 3, 4, 5].map((mood) => (
                        <g key={mood}>
                            <line className="mood-grid-line" x1={CHART.left} x2={CHART.width - CHART.right} y1={yFor(mood)} y2={yFor(mood)} />
                            <text className="mood-axis-label" x={CHART.left - 8} y={yFor(mood) + 4} textAnchor="end">{mood}</text>
                        </g>
                    ))}

                    {points.length > 1 && (
                        <polyline className="mood-line" points={points.map((point) => `${point.x},${point.y}`).join(' ')} />
                    )}

                    {points.map((point) => (
                        <circle className={`mood-point ${moodClass(Math.round(point.avg))}`} key={point.key} cx={point.x} cy={point.y} r="4.5">
                            <title>
                                {`${formatDayLabel(point.key)} - ${formatMood(point.avg)}/5 (${point.count} ${plural(point.count, 'wpis', 'wpisy', 'wpisów')})`}
                            </title>
                        </circle>
                    ))}

                    {axisLabels.map((key) => (
                        <text className="mood-axis-label" key={key} x={xFor(key)} y={CHART.height - 6} textAnchor="middle">
                            {formatDayLabel(key)}
                        </text>
                    ))}
                </svg>
            )}

            <div className="mood-grid">
                <div className="mood-block">
                    <h4>Rozkład ocen</h4>
                    {ranged.length === 0 && <p className="mood-empty">Brak wpisów.</p>}
                    {ranged.length > 0 && distribution.map((slot) => (
                        <div className="mood-dist-row" key={slot.score}>
                            <span className="mood-dist-score">{slot.score}</span>
                            <span className="mood-dist-track">
                                {slot.count > 0 && (
                                    <span className={`mood-dist-bar ${moodClass(slot.score)}`} style={{ width: `${(slot.count / maxCount) * 100}%` }} />
                                )}
                            </span>
                            <span className="mood-dist-count">{slot.count}</span>
                        </div>
                    ))}
                </div>

                <div className="mood-block">
                    <h4>Nastrój wg dnia tygodnia</h4>
                    {ranged.length === 0 && <p className="mood-empty">Brak wpisów.</p>}
                    {ranged.length > 0 && (
                        <div className="mood-weekdays">
                            {weekdays.map((day) => (
                                <div className="mood-weekday" key={day.label} title={day.avg === null ? 'Brak wpisów' : `${formatMood(day.avg)}/5 z ${day.count} ${plural(day.count, 'wpisu', 'wpisów', 'wpisów')}`}>
                                    <span className="mood-weekday-value">{day.avg === null ? '–' : formatMood(day.avg)}</span>
                                    <span className="mood-weekday-track">
                                        <span
                                            className={`mood-weekday-bar ${day.avg === null ? '' : moodClass(Math.round(day.avg))}`}
                                            style={{ height: day.avg === null ? '0%' : `${(day.avg / 5) * 100}%` }}
                                        />
                                    </span>
                                    <span className="mood-weekday-label">{day.label}</span>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                <div className="mood-block">
                    <h4>Najlepszy i najgorszy dzień</h4>
                    {!best && <p className="mood-empty">Brak wpisów.</p>}
                    {best && (
                        <>
                            <div className="mood-day-row">
                                <span className={`legend-dot ${moodClass(Math.round(best.avg))}`} />
                                <strong>{formatDayLabel(best.key)}</strong>
                                <span className="mood-day-value">{formatMood(best.avg)}/5</span>
                            </div>
                            <div className="mood-day-row">
                                <span className={`legend-dot ${moodClass(Math.round(worst.avg))}`} />
                                <strong>{formatDayLabel(worst.key)}</strong>
                                <span className="mood-day-value">{formatMood(worst.avg)}/5</span>
                            </div>
                            <p className="mood-note">{days.length} {plural(days.length, 'dzień', 'dni', 'dni')} z wpisem w tym okresie.</p>
                        </>
                    )}
                </div>

                <div className="mood-block">
                    <h4>Najczęstsze objawy</h4>
                    {symptoms.length === 0 && <p className="mood-empty">Żaden wpis nie ma objawów.</p>}
                    {symptoms.map((symptom) => (
                        <div className="mood-symptom-row" key={symptom.name}>
                            <span className={`tag-chip ${moodClass(Math.round(symptom.avgWith))}`}>{symptom.name}</span>
                            <span className="mood-symptom-stat">
                                {symptom.count}× · nastrój {formatMood(symptom.avgWith)}
                                {symptom.avgWithout !== null && ` (bez: ${formatMood(symptom.avgWithout)})`}
                            </span>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default MoodAnalysis;
