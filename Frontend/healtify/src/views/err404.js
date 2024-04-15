import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <div className="flex flex-col gap-2">
      <h1>404 Not Found</h1>
      <Link id='logreg' to="/">Home</Link>
    </div>
)};