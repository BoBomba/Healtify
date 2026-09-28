package com.healtify.healtify.dto;

import java.util.List;

/**
 * Porcja historii czatu.
 *
 * @param messages wiadomosci POSORTOWANE ROSNACO (najstarsza pierwsza), bo front rysuje
 *                 dymki od gory do dolu - baza oddaje je malejaco, odwracamy je po stronie serwera,
 *                 zeby kazdy klient nie musial pamietac o tym sam
 * @param hasMore  czy przed najstarsza z tej porcji jest jeszcze cokolwiek; front tylko na tej
 *                 podstawie pokazuje szewron "starsze wiadomosci"
 */
public record ChatPageResponse(
        List<ChatMessageResponse> messages,
        boolean hasMore
) {
}
