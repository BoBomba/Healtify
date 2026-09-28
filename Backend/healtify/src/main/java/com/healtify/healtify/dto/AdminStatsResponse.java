package com.healtify.healtify.dto;

/**
 * Liczniki na dashboard admina. Pacjenci to konta bez roli lekarza i admina -
 * same liczby, zadnych danych z dziennika.
 */
public record AdminStatsResponse(
        long patients,
        long doctors,
        long sharings,
        long appointments,
        long journalEntries
) {
}
