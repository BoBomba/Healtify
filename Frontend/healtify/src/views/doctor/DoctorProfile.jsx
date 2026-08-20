import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import Nav from '../../Components/Nav';
import { useDoctorGuard } from '../../utils/useDoctorGuard';
import { GetDoctorProfile, SaveDoctorProfile } from '../../service/doctorService';
import '../../css/dashboard.css';
import '../../css/data.css';
import '../../css/doctor.css';
import '../../css/profile.css';

const NOT_PROVIDED = 'Nie wprowadzono';

const EMPTY_FORM = {
    doctorName: '',
    title: '',
    specialization: '',
    licenseNumber: '',
    workplace: '',
    workAddress: '',
    phone: '',
};

const toForm = (profile) => {
    if (!profile) return EMPTY_FORM;

    return {
        doctorName: profile.doctorName ?? '',
        title: profile.title ?? '',
        specialization: profile.specialization ?? '',
        licenseNumber: profile.licenseNumber ?? '',
        workplace: profile.workplace ?? '',
        workAddress: profile.workAddress ?? '',
        phone: profile.phone ?? '',
    };
};

/** Puste pole ma trafić do bazy jako null, nie "". */
const toPayload = (form) => ({
    doctorName: form.doctorName.trim(),
    title: form.title.trim() || null,
    specialization: form.specialization.trim() || null,
    licenseNumber: form.licenseNumber.trim() || null,
    workplace: form.workplace.trim() || null,
    workAddress: form.workAddress.trim() || null,
    phone: form.phone.trim() || null,
});

/** Te same reguły pilnuje backend - tu po to, żeby komunikat pojawił się od razu. */
const validate = (form) => {
    const errors = [];

    if (!form.doctorName.trim()) {
        errors.push('Imię i nazwisko jest wymagane.');
    }
    if (form.licenseNumber.trim() && !/^[0-9]{7}$/.test(form.licenseNumber.trim())) {
        errors.push('Numer PWZ składa się z 7 cyfr.');
    }
    if (form.phone.trim() && !/^[0-9+ ()-]{6,20}$/.test(form.phone.trim())) {
        errors.push('Telefon może zawierać tylko cyfry, spacje i znaki + ( ) -');
    }

    return errors;
};

const profileRows = (profile) => {
    const data = profile || {};
    return [
        { label: 'Tytuł zawodowy', value: data.title },
        { label: 'Imię i nazwisko', value: data.doctorName },
        { label: 'Specjalizacja', value: data.specialization },
        { label: 'Numer PWZ', value: data.licenseNumber },
        { label: 'Placówka', value: data.workplace },
        { label: 'Adres', value: data.workAddress },
        { label: 'Telefon służbowy', value: data.phone },
    ];
};

/**
 * Dane lekarza - odpowiednik szczegółowych danych pacjenta, w tych samych dwóch trybach
 * (podgląd + formularz).
 *
 * `?setup=1` to pierwsze logowanie po nadaniu roli: strona otwiera się od razu w trybie
 * edycji i kończy przyciskiem "Gotowe", który zapisuje dane i przenosi na panel lekarza.
 * Zapis wisi na tym samym przycisku celowo - bez niego profil zostałby nieoznaczony
 * jako uzupełniony i formularz wracałby przy każdym kolejnym logowaniu.
 */
function DoctorProfile() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const isSetup = searchParams.get('setup') === '1';
    const startsInEditMode = isSetup || searchParams.get('edit') === '1';

    const { checking } = useDoctorGuard();

    const [profile, setProfile] = useState(null);
    const [form, setForm] = useState(EMPTY_FORM);
    const [editing, setEditing] = useState(startsInEditMode);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [errors, setErrors] = useState([]);
    const [notice, setNotice] = useState('');

    useEffect(() => {
        // Dopóki bramka sprawdza rolę, nie ma sensu pytać o profil - i tak może przekierować.
        if (checking) return;

        GetDoctorProfile()
            .then((fetched) => {
                setProfile(fetched);
                setForm(toForm(fetched));
            })
            .catch((error) => {
                console.log(error);
                setErrors(['Nie udało się pobrać danych. Spróbuj odświeżyć stronę.']);
            })
            .finally(() => setLoading(false));
    }, [checking]);

    const updateField = (field) => (event) => {
        setForm((previous) => ({ ...previous, [field]: event.target.value }));
    };

    const startEditing = () => {
        setForm(toForm(profile));
        setErrors([]);
        setNotice('');
        setEditing(true);
    };

    const goBack = () => {
        if (editing) {
            setForm(toForm(profile));
            setErrors([]);
            setEditing(false);
        } else {
            navigate('/doctor/dashboard');
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        const validationErrors = validate(form);
        setErrors(validationErrors);
        if (validationErrors.length > 0) return;

        setSaving(true);
        try {
            const saved = await SaveDoctorProfile(toPayload(form));
            setProfile(saved);
            setForm(toForm(saved));

            if (isSetup) {
                navigate('/doctor/dashboard');
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

    if (checking) {
        return (
            <div className="dashboard">
                <Nav />
                <main><p>Sprawdzanie uprawnień...</p></main>
            </div>
        );
    }

    return (
        <div className="dashboard">
            <Nav />
            <main>
                <div className="block-container">
                    <div className="block-row">
                        <div className="datablock profile-panel">
                            <h2 className="profile-title">Moje dane</h2>

                            {isSetup && (
                                <p className="profile-intro">
                                    Uzupełnij swoje dane zawodowe - pacjenci zobaczą je w wyszukiwarce
                                    lekarzy. Możesz zostawić pola puste i wrócić do nich później.
                                </p>
                            )}

                            {loading && <p>Wczytywanie...</p>}

                            {!loading && !editing && (
                                <>
                                    {notice && <div className="profile-notice">{notice}</div>}
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
                                        <span>Tytuł zawodowy</span>
                                        <input
                                            type="text"
                                            value={form.title}
                                            onChange={updateField('title')}
                                            placeholder="np. lek. / dr n. med."
                                        />
                                    </label>

                                    <label className="form-field">
                                        <span>Imię i nazwisko</span>
                                        <input
                                            type="text"
                                            value={form.doctorName}
                                            onChange={updateField('doctorName')}
                                            placeholder="np. Anna Wiśniewska"
                                        />
                                    </label>

                                    <label className="form-field">
                                        <span>Specjalizacja</span>
                                        <input
                                            type="text"
                                            value={form.specialization}
                                            onChange={updateField('specialization')}
                                            placeholder="np. Psychiatra"
                                        />
                                    </label>

                                    <label className="form-field">
                                        <span>Numer PWZ</span>
                                        <input
                                            type="text"
                                            inputMode="numeric"
                                            value={form.licenseNumber}
                                            onChange={updateField('licenseNumber')}
                                            placeholder="7 cyfr"
                                        />
                                    </label>

                                    <label className="form-field">
                                        <span>Telefon służbowy</span>
                                        <input
                                            type="tel"
                                            value={form.phone}
                                            onChange={updateField('phone')}
                                            placeholder="np. +48 12 345 67 89"
                                        />
                                    </label>

                                    <label className="form-field wide">
                                        <span>Placówka</span>
                                        <input
                                            type="text"
                                            value={form.workplace}
                                            onChange={updateField('workplace')}
                                            placeholder="np. Centrum Zdrowia Psychicznego"
                                        />
                                    </label>

                                    <label className="form-field wide">
                                        <span>Adres</span>
                                        <input
                                            type="text"
                                            value={form.workAddress}
                                            onChange={updateField('workAddress')}
                                            placeholder="np. ul. Długa 12, Kraków"
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

export default DoctorProfile;
