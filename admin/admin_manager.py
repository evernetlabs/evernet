from datetime import datetime

from pymongo.collection import Collection
import bcrypt


class AdminManager:
    def __init__(self, mongo: Collection):
        self.mongo = mongo

    def init(self, identifier: str, password: str):
        if not identifier.isalnum():
            raise Exception("Identifier must be alphanumeric")
        if len(identifier) < 3 or len(identifier) > 32:
            raise Exception("Identifier must be between 3 and 32 characters")
        if password.strip() == "":
            raise Exception("Password cannot be an empty string")
        if len(password) < 8 or len(password) > 128:
            raise Exception("Password must be between 8 and 128 characters")

        identifier = identifier.lower()

        if self.mongo.count_documents({}) != 0:
            raise Exception("You are not allowed to perform this action")

        admin_id = self.mongo.insert_one({
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            "created_at": datetime.now(),
            "updated_at": datetime.now(),
        }).inserted_id

        return {
            "id": str(admin_id),
            "identifier": identifier,
        }
