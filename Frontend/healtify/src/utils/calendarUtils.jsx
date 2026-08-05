export const MONTH_NAMES = [
    'Styczeń', 'Luty', 'Marzec', 'Kwiecień', 'Maj', 'Czerwiec',
    'Lipiec', 'Sierpień', 'Wrzesień', 'Październik', 'Listopad', 'Grudzień'
];

export const WEEKDAY_NAMES = ['Pon', 'Wt', 'Śr', 'Czw', 'Pt', 'Sob', 'Nd'];

export const formatDateKey = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

export const isSameDay = (a, b) => a && b && formatDateKey(a) === formatDateKey(b);

// Builds a 7-column week matrix for the given month, weeks starting on Monday.
export const getMonthMatrix = (year, month) => {
    const firstOfMonth = new Date(year, month, 1);
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    // JS getDay(): 0 = Sunday ... 6 = Saturday. Shift so Monday = 0.
    const leadingBlanks = (firstOfMonth.getDay() + 6) % 7;

    const cells = [];
    for (let i = 0; i < leadingBlanks; i++) {
        cells.push(null);
    }
    for (let day = 1; day <= daysInMonth; day++) {
        cells.push(new Date(year, month, day));
    }
    while (cells.length % 7 !== 0) {
        cells.push(null);
    }

    const weeks = [];
    for (let i = 0; i < cells.length; i += 7) {
        weeks.push(cells.slice(i, i + 7));
    }
    return weeks;
};
