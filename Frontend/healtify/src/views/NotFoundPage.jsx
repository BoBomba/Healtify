import React from 'react';
import { Link } from 'react-router-dom';
import '../css/Global.css';

export default function NotFoundPage() {
  return (
    <div className="not-found">
      <div className="main-container">
        <h1 style={{ fontSize: '4em', margin: 0 }}>404</h1>
        <p style={{ textAlign: 'center', padding: '0 1em' }}>
          Strona, której szukasz, nie istnieje.
        </p>
        <Link id="logreg" to="/">Wróć na stronę główną</Link>
      </div>
    </div>
  );
}