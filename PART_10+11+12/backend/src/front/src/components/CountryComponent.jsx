import React, { useState, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {faTrash, faEdit, faPlus, faSave} from '@fortawesome/free-solid-svg-icons'
import Alert from './Alert'
import BackendService from "../services/BackendService";
import {useNavigate, useParams} from 'react-router-dom';
import {Form} from "react-bootstrap";
import axios from "axios";


const CountryComponent = props => {

    const [name, setName] = useState('');
    const [country, setCountry] = useState({});
    const { id } = useParams()
    const [page, setPage] = useState(0);
    const [totalCount, setTotalCount] = useState(0);
    const limit = 2;

    const navigate = useNavigate();

    const onPageChanged = cp => {
        refreshCountries(cp - 1)
    }
    useEffect(() => {
        if (id > 0) {
            BackendService.retrieveCountry(id)
                .then(res => {
                    setCountry(res.data);
                })
        }
    }, [id]);

    const onSubmit = country => {
        if (id > 0) BackendService.updateCountry({id,name}).then(() => refreshCountries(page));
        else BackendService.createCountry({name})
            .then(() => refreshCountries(page));
    }

    const handleChange = e => {
        setName(e.target.value);
    }
    const refreshCountries = cp => {
        BackendService.retrieveAllCountries(cp, limit)
            .then(
                resp => {
                    setTotalCount(resp.data.totalElements);
                    setPage(cp);
                }
            )
            .catch(() => {
                setTotalCount(0);
            })
        navigate('/countries');
    }


    return (
        <div className="m-4">
            <div className="row my-2">
                <h3>Страна</h3>
            </div>
            <div className="row my-2 me-0">
                <table className="table table-sm">
                    <thead className="thead-light">
                    <tr>
                        <th>Название</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        <td>
                            <input type="text" onChange={handleChange} placeholder={country.name} />
                            <div>
                                <div className="btn-group  ms-auto">
                                    <button className="btn btn-primary" onClick={onSubmit}>
                                        <FontAwesomeIcon icon={faSave}  /> Сохранить
                                    </button>
                                </div>
                            </div>
                        </td>
                    }
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default CountryComponent;