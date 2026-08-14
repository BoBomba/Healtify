import router from '../Routing';

/**
 * Przejście na inną stronę bez przeładowania aplikacji.
 *
 * useNavigate to hook - działa tylko w komponentach, a przekierowania wychodzą
 * też z serwisów. 
 * Router z createBrowserRouter zajmuje się serwisami, serwisy wołają jego navigate.
 * A w komponentach i hookach zostaje useNavigate.
 *
 * Domyślnie podmieniamy wpis w historii zamiast dokładać nowy - to dla przekierowan,
 * na które ma nie wracać "wstecz" (np. formularz logowania po zalogowaniu, panel po wygaśnięciu sesji).
 */
export const navigateTo = (path, options = { replace: true }) =>
    router.navigate(path, options);
