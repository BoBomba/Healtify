import React from 'react';
import '../css/profile.css';

/** Jeden komunikat na brak danych - używany wszędzie */
export const NOT_PROVIDED = 'Nie wprowadzono';

/** Backend wysyła LocalDate jako "1998-03-14". */
const formatDate = (isoDate) => {
    const [year, month, day] = isoDate.split('-');
    return `${day}.${month}.${year}`;
};

/** Odmiana "rok / lata / lat" */
const yearsLabel = (age) => {
    const lastDigit = age % 10;
    const lastTwoDigits = age % 100;

    if (age === 1) return 'rok';
    if (lastDigit >= 2 && lastDigit <= 4 && !(lastTwoDigits >= 12 && lastTwoDigits <= 14)) {
        return 'lata';
    }
    return 'lat';
};

/** Widełki WHO dla lepszej interpretacji */
const bmiCategory = (bmi) => {
    if (bmi < 18.5) return 'niedowaga';
    if (bmi < 25) return 'prawidłowa';
    if (bmi < 30) return 'nadwaga';
    return 'otyłość';
};

/**
 * Profil rozpisany na pary etykieta-wartość. Wartość null to brak danych,
 * podmianę na NOT_PROVIDED robi dopiero renderowanie.
 */
export const profileRows = (profile) => {
    const data = profile || {};
    const hasAge = data.age !== null && data.age !== undefined;

    return [
        { label: 'Imię i nazwisko', value: data.fullName },
        {
            label: 'Wiek',
            value: hasAge
                ? `${data.age} ${yearsLabel(data.age)}` +
                  (data.dateOfBirth ? ` (ur. ${formatDate(data.dateOfBirth)})` : '')
                : null,
        },
        { label: 'Płeć', value: data.gender },
        { label: 'Telefon kontaktowy', value: data.phone },
        { label: 'Wzrost', value: data.heightCm ? `${data.heightCm} cm` : null },
        { label: 'Waga', value: data.weightKg ? `${data.weightKg} kg` : null },
        {
            // toFixed, bo JSON-owe 21.0 wraca z JS-a jako "21" i obok "23.3" wygląda na błąd.
            label: 'BMI',
            value: data.bmi
                ? `${Number(data.bmi).toFixed(1)} (${bmiCategory(Number(data.bmi))})`
                : null,
        },
        { label: 'Grupa krwi', value: data.bloodType },
        { label: 'Alergie', value: data.allergies },
        { label: 'Choroby przewlekłe', value: data.chronicDiseases },
        { label: 'Przyjmowane leki', value: data.medications },
    ];
};

/** Czytelny podgląd profilu - dashboard, zakładka Dane i tryb podglądu na /data/profile. */
function PatientDetails({ profile }) {
    return (
        <dl className="details-list">
            {profileRows(profile).map(({ label, value }) => (
                <div className="detail-row" key={label}>
                    <dt className="detail-label">{label}</dt>
                    <dd className={value ? 'detail-value' : 'detail-value missing'}>
                        {value || NOT_PROVIDED}
                    </dd>
                </div>
            ))}
        </dl>
    );
}

export default PatientDetails;
