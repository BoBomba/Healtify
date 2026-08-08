import { useEffect, useState } from 'react';
import { validateToken } from '../service/authService';
import { GetDoctorProfile } from '../service/doctorService';
import { getCurrentUser } from '../service/userService';

/**
 * Wpuszcza na strony /doctor/* tylko lekarza.
 *
 * Front pyta backend o profil lekarza,
 * a ten oddaje 403 każdemu bez roli ROLE_DOCTOR. Ukrycie linków w menu to tylko
 * wygoda; prawdziwą blokadą jest @PreAuthorize na /api/doctor.
 */
/**
 * 403 z /api/doctor/me ma dwie przyczyny: albo nie ma roli ROLE_DOCTOR, albo rolę ma, ale nie ma
 * profilu w tabeli doctors (nadanie roli ręcznie w bazie zamiast przez AdminPanel). 
 * Dopytujemy więc /api/user/me i mówimy wprost, co jest nie tak.
 */
async function explainRejection() {
    try {
        const user = await getCurrentUser();
        if (user.doctor === true) {
            return 'To konto ma rolę lekarza, ale nie ma profilu lekarza w bazie. '
                + 'Admin musi nadać rolę przez AdminPanel ("Nadaj rolę lekarza"), '
                + 'bo to on zakłada wpis w tabeli doctors.';
        }
    } catch (error) {
        console.log(error);
    }
    return 'Ta część aplikacji jest dostępna tylko dla lekarzy.';
}

export function useDoctorGuard() {
    const [doctor, setDoctor] = useState(null);
    const [checking, setChecking] = useState(true);

    useEffect(() => {
        async function check() {
            await validateToken();
            try {
                setDoctor(await GetDoctorProfile());
            } catch (error) {
                console.log(error);
                alert(await explainRejection());
                window.location.href = '/dashboard';
                return;
            } finally {
                setChecking(false);
            }
        }
        check();
    }, []);

    return { doctor, checking };
}
