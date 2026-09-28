import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import Nav from '../../Components/Nav';
import PatientDetails from '../../Components/PatientDetails';
import { validateToken } from '../../service/authService';
import { GetPatientProfile, SavePatientProfile } from '../../service/dataService';
import '../../css/dashboard.css';
import '../../css/data.css';
import '../../css/profile.css';

const GENDERS = ['Kobieta', 'Mężczyzna', 'Inna', 'Wolę nie podawać'];
const BLOOD_TYPES = ['0 Rh-', '0 Rh+', 'A Rh-', 'A Rh+', 'B Rh-', 'B Rh+', 'AB Rh-', 'AB Rh+'];

const EMPTY_FORM = {
    fullName: '',
    dateOfBirth: '',
    gender: '',
    phone: '',
    heightCm: '',
    weightKg: '',
    bloodType: '',
    allergies: '',
    chronicDiseases: '',
    medications: '',
};

/** Odpowiedź backendu -> stan formularza. Null nie nadaje się na `value` inputa. */
const toForm = (profile) => {
    if (!profile) return EMPTY_FORM;

    return {
        fullName: profile.fullName ?? '',
        dateOfBirth: profile.dateOfBirth ?? '',
        gender: profile.gender ?? '',
        phone: profile.phone ?? '',
        heightCm: profile.heightCm ?? '',
        weightKg: profile.weightKg ?? '',
        bloodType: profile.bloodType ?? '',
        allergies: profile.allergies ?? '',
        chronicDiseases: profile.chronicDiseases ?? '',
        medications: profile.medications ?? '',
    };
};

/** Stan formularza -> ciało żądania. Puste pole do bazy jako null, nie "". */
const toPayload = (form) => ({
    fullName: form.fullName.trim() || null,
    dateOfBirth: form.dateOfBirth || null,
    gender: form.gender || null,
    phone: form.phone.trim() || null,
    heightCm: form.heightCm === '' ? null : Number(form.heightCm),
    weightKg: form.weightKg === '' ? null : Number(form.weightKg),
    bloodType: form.bloodType || null,
    allergies: form.allergies.trim() || null,
    chronicDiseases: form.chronicDiseases.trim() || null,
    medications: form.medications.trim() || null,
});

/**
 * Te same zakresy pilnuje backend - tutaj powtarzamy, żeby użytkownik dostał
 * komunikat po polsku od razu, bez strzału do serwera.
 */
const validate = (form) => {
    const errors = [];

    if (form.dateOfBirth && form.dateOfBirth >= new Date().toISOString().slice(0, 10)) {
        errors.push('Data urodzenia musi być z przeszłości.');
    }

    const height = Number(form.heightCm);
    if (form.heightCm !== '' && (Number.isNaN(height) || height < 50 || height > 260)) {
        errors.push('Wzrost musi być z zakresu 50-260 cm.');
    }

    const weight = Number(form.weightKg);
    if (form.weightKg !== '' && (Number.isNaN(weight) || weight < 1 || weight > 500)) {
        errors.push('Waga musi być z zakresu 1-500 kg.');
    }

    if (form.phone.trim() && !/^[0-9+ ()-]{6,20}$/.test(form.phone.trim())) {
        errors.push('Telefon może zawierać tylko cyfry, spacje i znaki + ( ) -');
    }

    return errors;
};

/**
 * Szczegółowe dane pacjenta - jedna strona w dwóch trybach: podgląd (lista danych
 * + przycisk "Edytuj") i edycja.
 *
 * Wejście z `?setup=1` to pierwsze logowanie: strona otwiera się od razu w trybie
 * edycji, po tym zapisuje dane i przenosi na dashboard.
 * Zapis jest tam wpięty w ten przycisk celowo - samo przejście dalej bez zapisu
 * zostawi profil pusty i przy kolejnym logowaniu formularz wyskoczy znowu.
 *
 * `?edit=1` to zwykłe wejście prosto w formularz - używa go "Edytuj" z podglądu
 * w Ogólnych Danych, żeby nie trzeba było klikać "Edytuj" dwa razy.
 */
function PatientProfile() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const isSetup = searchParams.get('setup') === '1';
    const startsInEditMode = isSetup || searchParams.get('edit') === '1';

    const [profile, setProfile] = useState(null);
    const [form, setForm] = useState(EMPTY_FORM);
    const [editing, setEditing] = useState(startsInEditMode);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [errors, setErrors] = useState([]);
    const [notice, setNotice] = useState('');

    useEffect(() => {
        validateToken();
        GetPatientProfile()
            .then((fetched) => {
                setProfile(fetched);
                setForm(toForm(fetched));
            })
            .catch((error) => {
                console.log(error);
                setErrors(['Nie udało się pobrać danych. Spróbuj odświeżyć stronę.']);
            })
            .finally(() => setLoading(false));
    }, []);

    const updateField = (field) => (event) => {
        setForm((previous) => ({ ...previous, [field]: event.target.value }));
    };

    const startEditing = () => {
        setForm(toForm(profile));
        setErrors([]);
        setNotice('');
        setEditing(true);
    };

    /** W trybie edycji wraca do podglądu, w podglądzie - do zakładki Dane. */
    const goBack = () => {
        if (editing) {
            setForm(toForm(profile));
            setErrors([]);
            setEditing(false);
        } else {
            navigate('/data');
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        const validationErrors = validate(form);
        setErrors(validationErrors);
        if (validationErrors.length > 0) return;

        setSaving(true);
        try {
            const saved = await SavePatientProfile(toPayload(form));
            setProfile(saved);
            setForm(toForm(saved));

            if (isSetup) {
                navigate('/dashboard');
                return;
            }

            setEditing(false);
            setNotice('Dane zostały zapisane.');
        } catch (error) {
            console.log(error);
            setErrors(['Nie udało się zapisać danych. Sprawdź wprowadzone wartości i spróbuj ponownie.']);
        } finally {
            setSaving(false);
        }
    };

    return (
        <div>
            <Nav />
            <main>
                <div className="block-container">
                    <div className="block-row">
                        <div className="datablock profile-panel">
                            <h2 className="profile-title">Szczegółowe dane</h2>

                            {isSetup && (
                                <p className="profile-intro">
                                    Uzupełnij swoje dane, żeby dashboard i lekarz widzieli pełny obraz.
                                    Możesz zostawić pola puste i wrócić do nich później.
                                </p>
                            )}

                            {loading && <p>Wczytywanie...</p>}

                            {!loading && !editing && (
                                <>
                                    {notice && <div className="profile-notice">{notice}</div>}
                                    <PatientDetails profile={profile} />
                                    <button type="button" className="big-btn" onClick={startEditing}>
                                        Edytuj
                                    </button>
                                    <button type="button" className="big-btn secondary" onClick={goBack}>
                                        Powrót
                                    </button>
                                </>
                            )}

                            {!loading && editing && (
                                <form className="profile-form" onSubmit={handleSubmit} noValidate>
                                    <label className="form-field">
                                        <span>Imię i nazwisko</span>
                                        <input
                                            type="text"
                                            value={form.fullName}
                                            onChange={updateField('fullName')}
                                            placeholder="np. Jan Kowalski"
                                        />
                                    </label>

                                    <label className="form-field">
                                        <span>Data urodzenia</span>
                                        <input
                                            type="date"
                                            value={form.dateOfBirth}
                                            onChange={updateField('dateOfBirth')}
                                            max={new Date().toISOString().slice(0, 10)}
                                        />
                                    </label>

                                    <label className="form-field">
                                        <span>Płeć</span>
                                        <select value={form.gender} onChange={updateField('gender')}>
                                            <option value="">Nie wprowadzono</option>
                                            {GENDERS.map((gender) => (
                                                <option key={gender} value={gender}>{gender}</option>
                                            ))}
                                        </select>
                                    </label>

                                    <label className="form-field">
                                        <span>Telefon kontaktowy</span>
                                        <input
                                            type="tel"
                                            value={form.phone}
                                            onChange={updateField('phone')}
                                            placeholder="np. +48 600 100 200"
                                        />
                                    </label>

                                    <label className="form-field">
                                        <span>Wzrost (cm)</span>
                                        <input
                                            type="number"
                                            min="50"
                                            max="260"
                                            value={form.heightCm}
                                            onChange={updateField('heightCm')}
                                            placeholder="np. 180"
                                        />
                                    </label>

                                    <label className="form-field">
                                        <span>Waga (kg)</span>
                                        <input
                                            type="number"
                                            min="1"
                                            max="500"
                                            step="0.1"
                                            value={form.weightKg}
                                            onChange={updateField('weightKg')}
                                            placeholder="np. 75"
                                        />
                                    </label>

                                    <label className="form-field">
                                        <span>Grupa krwi</span>
                                        <select value={form.bloodType} onChange={updateField('bloodType')}>
                                            <option value="">Nie wprowadzono</option>
                                            {BLOOD_TYPES.map((bloodType) => (
                                                <option key={bloodType} value={bloodType}>{bloodType}</option>
                                            ))}
                                        </select>
                                    </label>

                                    <label className="form-field wide">
                                        <span>Alergie</span>
                                        <textarea
                                            rows="2"
                                            value={form.allergies}
                                            onChange={updateField('allergies')}
                                            placeholder="np. pyłki traw, penicylina"
                                        />
                                    </label>

                                    <label className="form-field wide">
                                        <span>Choroby przewlekłe</span>
                                        <textarea
                                            rows="2"
                                            value={form.chronicDiseases}
                                            onChange={updateField('chronicDiseases')}
                                            placeholder="np. astma"
                                        />
                                    </label>

                                    <label className="form-field wide">
                                        <span>Przyjmowane leki</span>
                                        <textarea
                                            rows="2"
                                            value={form.medications}
                                            onChange={updateField('medications')}
                                            placeholder="np. sertralina 50 mg"
                                        />
                                    </label>

                                    {errors.length > 0 && (
                                        <div id="messages" className="profile-errors">
                                            {errors.map((error) => (
                                                <div key={error}>{error}</div>
                                            ))}
                                        </div>
                                    )}

                                    <button type="submit" className="big-btn" disabled={saving}>
                                        {saving ? 'Zapisywanie...' : (isSetup ? 'Gotowe' : 'Zapisz')}
                                    </button>

                                    {!isSetup && (
                                        <button
                                            type="button"
                                            className="big-btn secondary"
                                            onClick={goBack}
                                            disabled={saving}
                                        >
                                            Powrót
                                        </button>
                                    )}
                                </form>
                            )}
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}

export default PatientProfile;
