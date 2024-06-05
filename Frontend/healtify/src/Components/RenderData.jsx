import React from 'react'
import '../css/dashboard.css'

export const RenderData = (data, parentKey = '') => {
    return Object.entries(data).map(([key, value]) => {
        const newKey = parentKey ? `${parentKey}.${key}` : key;
        if (key === 'userAccount') {
            return null;
        }
        if (value !== null && typeof value === 'object') {
            return RenderData(value, newKey);
        } else {
            return <p key={newKey}>{`${newKey}: ${value}`}</p>;
        }
    });
};

export const RenderAccountData = (data, parentKey = '') => {
    return Object.entries(data).map(([key, value]) => {
        const newKey = parentKey ? `${parentKey}.${key}` : key;
        if (key ==! 'userAccount') {
            return null;
        }
        if (value !== null && typeof value === 'object') {
            return RenderData(value, newKey);
        } else {
            return <p key={newKey}>{`${newKey}: ${value}`}</p>;
        }
    });
}