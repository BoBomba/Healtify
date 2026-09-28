// filtrowanie list wpisów i wizyt.

/** Bez ogonków i wielkości liter */
export const normalize = (value) =>
    (value ?? '')
        .toString()
        .toLowerCase()
        // ł nie rozkłada się przez NFD, więc leci osobno. 
        .replace(/ł/g, 'l')
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '');

/**
 * Każde słowo musi trafić w którekolwiek pole,
 * np. "lek praca" znajdzie wpis z objawem "Lęk" i tytułem "Praca". 
 * Pusta fraza przepuszcza wszystko.
 */
export const matchesQuery = (query, ...fields) => {
    const words = normalize(query).trim().split(/\s+/).filter(Boolean);
    if (words.length === 0) return true;

    const haystack = fields.flat().map(normalize);
    return words.every((word) => haystack.some((field) => field.includes(word)));
};

/** Zakres dat po kluczu "YYYY-MM-DD" */
export const matchesDateRange = (isoAt, from, to) => {
    const day = (isoAt ?? '').slice(0, 10);
    if (from && day < from) return false;
    if (to && day > to) return false;
    return true;
};
