import React, {useState, useEffect} from 'react';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faTrash, faEdit, faPlus, faSave} from '@fortawesome/free-solid-svg-icons'
import Alert from './Alert'
import BackendService from "../services/BackendService";
import {useNavigate, useParams} from 'react-router-dom';
import {Form} from "react-bootstrap";
import axios from "axios";


const ArtistComponent = props => {

    const [countries, setCountries] = useState([]);
    const [name, setName] = useState('');
    const [age, setAge] = useState('');
    const [country, setCountry] = useState({});
    const [artist, setArtist] = useState({});
    const {id} = useParams()
    const [page, setPage] = useState(0);
    const [totalCount, setTotalCount] = useState(0);
    const limit = 2;

    const navigate = useNavigate();

    const onPageChanged = cp => {
        refreshArtists(cp - 1)
    }
    useEffect(() => {
        if (id > 0) {
            BackendService.retrieveArtist(id)
                .then(res => {
                    setArtist(res.data);
                })
        }
        BackendService.retrieveAllCountries(0,100).then(res => {
            setCountries(res.data.content);
        })
    }, [id]);

    const onSubmit = artist => {
        if (id > 0) BackendService.updateArtist({id,name,country,age}).then(() => refreshArtists(page));
        else BackendService.createArtist({name,country,age})
            .then(() => refreshArtists(page));
    }

    const handleChange = e => {
        setName(e.target.name);
        setAge(e.target.age);
    }
    const refreshArtists = cp => {
        BackendService.retrieveAllArtists(cp, limit)
            .then(
                resp => {
                    setTotalCount(resp.data.totalElements);
                    setPage(cp);
                }
            )
            .catch(() => {
                setTotalCount(0);
            })
        navigate('/artists');
    }


    return (
        <div className="m-4">
            <div className="row my-2">
                <h3>Художники</h3>
            </div>
            <div className="row my-2 me-0">
                <table className="table table-sm">
                    <thead className="thead-light">
                    </thead>
                    <tbody>
                    {
                        <td>
                            <div>
                                <label htmlFor="name">Имя</label><br/>
                                <input type="text"
                                       name="name" placeholder={artist.name}
                                       onChange={handleChange}/>
                            </div>
                            <div>
                                <label htmlFor="country">Страна</label><br/>
                                <select name="country" onChange={() => setCountry({id:country.id,name: country.name})}>
                                    {
                                        countries && countries.map((country, index) =>
                                            <option value={country.id}>{country.name}</option>
                                        )
                                    }
                                </select>
                            </div>
                            <div>
                                <label htmlFor="age">Век</label><br/>
                                <input type="text"
                                       name="age" placeholder={artist.age}
                                       onChange={handleChange}/>
                            </div>
                            <div className="mt-2">
                                <button className="btn btn-primary" onClick={onSubmit}>
                                    <FontAwesomeIcon icon={faSave}/> Сохранить
                                </button>
                            </div>
                        </td>
                    }
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default ArtistComponent;