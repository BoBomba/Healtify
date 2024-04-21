import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../css/Global.css';

function MyMessage() {
    const [message, setMessage] = useState("");
    const [error, setError] = useState(false);
    const [inputMessage, setInputMessage] = useState("");

    const sendMessage = () => {
        axios.post('http://localhost:8080/message', { message: inputMessage })
            .then(response => {
                setMessage(response.data);
                setError(false);
            })
            .catch(error => {
                setError(true);
            });
    };

    useEffect(() => {
        axios.get('http://localhost:8080/message')
            .then(response => {
                setMessage(response.data);
                setError(false);
            })
            .catch(error => {
                setError(true);
            });
    }, []);

    return (
        <div>
            <div id='input'>
            <input type="text" value={inputMessage} onChange={e => setInputMessage(e.target.value)} />
            </div>
            <button onClick={sendMessage}>Send Message</button>
            <div>{error && <div>There was an error</div>}</div>
            <div id='messages'>{message}</div>
        </div>
    );
}

export default MyMessage;