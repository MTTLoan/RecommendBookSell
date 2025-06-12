import pandas as pd
import numpy as np
from scipy.sparse import csr_matrix
from sklearn.neighbors import NearestNeighbors
import json
import sys
import requests

# Lấy dữ liệu từ API
def fetch_data():
    try:
        books_response = requests.get('http://localhost:5000/api/books')
        reviews_response = requests.get('http://localhost:5000/api/reviews')
        
        if books_response.status_code != 200 or reviews_response.status_code != 200:
            raise Exception("Lỗi khi gọi API")
        
        books = pd.DataFrame(books_response.json())
        reviews = pd.DataFrame(reviews_response.json())
        
        return books, reviews
    except Exception as e:
        raise Exception(f"Lỗi khi lấy dữ liệu từ API: {str(e)}")

# Tạo ma trận thưa
def create_matrix(df):
    N = len(df['userId'].unique())
    M = len(df['bookId'].unique())
    
    user_mapper = dict(zip(np.unique(df["userId"]), list(range(N))))
    book_mapper = dict(zip(np.unique(df["bookId"]), list(range(M))))
    user_inv_mapper = dict(zip(list(range(N)), np.unique(df["userId"])))
    book_inv_mapper = dict(zip(list(range(M)), np.unique(df["bookId"])))
    
    user_index = [user_mapper[i] for i in df['userId']]
    book_index = [book_mapper[i] for i in df['bookId']]
    X = csr_matrix((df["rating"], (book_index, user_index)), shape=(M, N))
    
    return X, user_mapper, book_mapper, user_inv_mapper, book_inv_mapper

# Tìm sách tương tự
def find_similar_books(book_id, X, k, metric='cosine', book_mapper=None, book_inv_mapper=None, books=None):
    if book_id not in book_mapper:
        return []
    
    book_ind = book_mapper[book_id]
    book_vec = X[book_ind]
    k += 1
    kNN = NearestNeighbors(n_neighbors=k, algorithm="brute", metric=metric)
    kNN.fit(X)
    book_vec = book_vec.reshape(1, -1) # Chuyển đổi thành ma trận 2D
    neighbour = kNN.kneighbors(book_vec, return_distance=False)
    
    neighbour_ids = [book_inv_mapper[n] for n in neighbour[0]]
    neighbour_ids.pop(0)  # Loại bỏ sách đầu vào
    
    # Lấy thông tin chi tiết của sách được gợi ý
    recommended_details = []
    for book_id in neighbour_ids:
        book_info = books[books['id'] == book_id][['id', 'name', 'averageRating']].to_dict('records')
        if book_info:
            recommended_details.append(book_info[0])
    
    return recommended_details

# Gợi ý sách cho người dùng
def recommend_books_for_user(user_id, X, user_mapper, book_mapper, book_inv_mapper, reviews, books, k=10):
    df1 = reviews[reviews['userId'] == user_id]
    
    if df1.empty:
        return []
    
    book_id = df1[df1['rating'] == max(df1['rating'])]['bookId'].iloc[0]
    similar_ids = find_similar_books(book_id, X, k, book_mapper=book_mapper, book_inv_mapper=book_inv_mapper, books=books)
    
    return similar_ids

# Load dữ liệu từ API
books, reviews = fetch_data()
X, user_mapper, book_mapper, user_inv_mapper, book_inv_mapper = create_matrix(reviews)

# Hàm chính để gọi từ Node.js
if __name__ == "__main__":
    if len(sys.argv) < 2:
        print(json.dumps({"error": "Vui lòng cung cấp userId hoặc bookId"}))
        sys.exit(1)
    
    try:
        input_type = sys.argv[1]
        if input_type == "--user":
            if len(sys.argv) != 3:
                print(json.dumps({"error": "Vui lòng cung cấp userId"}))
                sys.exit(1)
            user_id = int(sys.argv[2])
            recommended_books = recommend_books_for_user(user_id, X, user_mapper, book_mapper, book_inv_mapper, reviews, books, k=20)
            print(json.dumps({"recommended_books": recommended_books}))
        elif input_type == "--book":
            if len(sys.argv) != 3:
                print(json.dumps({"error": "Vui lòng cung cấp bookId"}))
                sys.exit(1)
            book_id = int(sys.argv[2])
            recommended_books = find_similar_books(book_id, X, k=20, metric='cosine', book_mapper=book_mapper, book_inv_mapper=book_inv_mapper, books=books)
            print(json.dumps({"recommended_books": recommended_books}))
        else:
            print(json.dumps({"error": "Loại đầu vào không hợp lệ. Sử dụng --user hoặc --book"}))
            sys.exit(1)
    except Exception as e:
        print(json.dumps({"error": str(e)}))