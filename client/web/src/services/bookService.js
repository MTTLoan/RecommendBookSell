import axios from 'axios';
import { API_BASE_URL } from '../utils/constants';

const API_URL = 'http://localhost:5000/api/books/all-book';

export const fetchBooks = async () => {
  const res = await axios.get(API_URL);
  return res.data.book;
};

export const fetchBookDetail = async (id) => {
  const res = await axios.get(`${API_BASE_URL}/books/book-detail/${id}`);
  return res.data.book;
};

export const addBook = async (bookData) => {
  const res = await axios.post(`${API_BASE_URL}/books/add-book`, bookData);
  return res.data.book;
};

export const fetchNextBookId = async () => {
  const res = await axios.get(`${API_BASE_URL}/books/next-id`);
  return res.data;
};

export const peekNextBookId = async () => {
  const res = await axios.get(`${API_BASE_URL}/books/peek-next-id`);
  return res.data;
};

export const updateBook = async (id, bookData) => {
  const res = await axios.put(`${API_BASE_URL}/books/update-book/${id}`, bookData);
  return res.data.book;
};

export const deleteBook = async (id) => {
  const res = await axios.delete(`${API_BASE_URL}/books/delete-book/${id}`);
  return res.data;
};