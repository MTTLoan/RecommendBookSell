from pymongo import MongoClient
from dotenv import load_dotenv
import os

load_dotenv()

MONGO_URI = os.getenv("MONGO_URI")

client = None
db = None

def connect_db():
    global client, db
    try:
        client = MongoClient(MONGO_URI)
        db = client.get_database("ecommerce")
        print("MongoDB connected")
        return db
    except Exception as e:
        print(f"MongoDB connection error: {e}")
        raise

def get_db():
    if not db:
        connect_db()
    return db

def close_db():
    global client
    if client:
        client.close()
        print("MongoDB connection closed")