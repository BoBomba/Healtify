import React, { useState } from 'react';
import '../css/search.css';

/**
 * Pasek wyszukiwania nad listą: fraza, zwijane filtry (children) i licznik wyników.
 * tutaj tylko obudowa, żeby wpisy i wizyty wyglądały tak samo.
 */
function SearchBar({
    query,
    onQueryChange,
    placeholder,
    onReset,
    activeFilterCount = 0,
    summary,
    children,
}) {
    // Filtry startują zwinięte
    const [showFilters, setShowFilters] = useState(false);

    const isDirty = query !== '' || activeFilterCount > 0;

    return (
        <div className="search-panel">
            <div className="search-row">
                <input
                    type="search"
                    className="search-input"
                    placeholder={placeholder}
                    value={query}
                    onChange={(e) => onQueryChange(e.target.value)}
                />
                {children && (
                    <button
                        type="button"
                        className="modal-btn secondary small"
                        onClick={() => setShowFilters((prev) => !prev)}
                    >
                        Filtry{activeFilterCount > 0 ? ` (${activeFilterCount})` : ''}
                    </button>
                )}
                <button
                    type="button"
                    className="modal-btn secondary small"
                    onClick={onReset}
                    disabled={!isDirty}
                >
                    Wyczyść
                </button>
            </div>

            {children && showFilters && <div className="search-filters">{children}</div>}

            {summary && <p className="search-summary">{summary}</p>}
        </div>
    );
}

export default SearchBar;
