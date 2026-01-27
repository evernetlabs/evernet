from cryptography.hazmat.primitives.asymmetric.ed25519 import Ed25519PrivateKey
from pymongo.collection import Collection

from exception.types import ClientException, NotFoundException
from utils.ed25519 import generate_ed25519_keys, private_key_to_string, public_key_to_string, string_to_private_key
from utils.time import current_datetime


class NodeService:
    def __init__(self, mongo: Collection):
        self.mongo = mongo

    def create(self, identifier: str, display_name: str, description: str, open: bool, creator: str) -> dict:
        if self.mongo.count_documents({
            "identifier": identifier
        }) > 0:
            raise ClientException(f"Node {identifier} already exists")

        signing_private_key, signing_public_key = generate_ed25519_keys()

        self.mongo.insert_one({
            "identifier": identifier,
            "display_name": display_name,
            "description": description,
            "open": open,
            "signing_public_key": public_key_to_string(signing_public_key),
            "signing_private_key": private_key_to_string(signing_private_key),
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime(),
        })

        return {
            "identifier": identifier
        }

    def fetch(self, page: int = 0, size: int = 50) -> list[dict]:
        nodes = self.mongo.find({}).skip(page * size).limit(size)
        return [self.to_dict(node) for node in nodes]

    def fetch_open(self, page: int = 0, size: int = 50) -> list[dict]:
        nodes = self.mongo.find({"open": True}).skip(page * size).limit(size)
        return [self.to_dict(node) for node in nodes]

    def get(self, identifier: str) -> dict:
        node = self.mongo.find_one({"identifier": identifier})

        if not node:
            raise NotFoundException(f"Node {identifier} not found")

        return self.to_dict(node)

    def update(self, identifier: str, display_name: str, description: str) -> dict:
        fields = {
            "updated_at": current_datetime(),
            "description": description,
        }

        if display_name:
            fields["display_name"] = display_name

        result = self.mongo.update_one({
            "identifier": identifier
        }, {
            "$set": fields
        })

        if result.matched_count == 0:
            raise NotFoundException(f"Node {identifier} not found")

        return {
            "identifier": identifier
        }

    def update_open_flag(self, identifier: str, open: bool) -> dict:
        result = self.mongo.update_one({
            "identifier": identifier
        }, {
            "$set": {
                "open": open,
                "updated_at": current_datetime(),
            }
        })

        if result.matched_count == 0:
            raise NotFoundException(f"Node {identifier} not found")

        return {
            "identifier": identifier
        }

    def reset_signing_keys(self, identifier: str) -> dict:
        signing_private_key, signing_public_key = generate_ed25519_keys()
        fields = {
            "updated_at": current_datetime(),
            "signing_private_key": private_key_to_string(signing_private_key),
            "signing_public_key": public_key_to_string(signing_public_key),
        }

        result = self.mongo.update_one({
            "identifier": identifier
        }, {
            "$set": fields
        })

        if result.matched_count == 0:
            raise NotFoundException(f"Node {identifier} not found")

        return {
            "identifier": identifier
        }

    def delete(self, identifier: str) -> dict:
        result = self.mongo.delete_one({
            "identifier": identifier
        })

        if result.deleted_count:
            raise NotFoundException(f"Node {identifier} not found")

        return {
            "identifier": identifier
        }

    def exists(self, identifier: str) -> bool:
        return self.mongo.count_documents({"identifier": identifier}) > 0

    def get_signing_private_key(self, identifier: str) -> Ed25519PrivateKey:
        node = self.mongo.find_one({"identifier": identifier})
        if not node:
            raise NotFoundException(f"Node {identifier} not found")
        return string_to_private_key(node.get("signing_private_key"))

    @staticmethod
    def to_dict(self):
        return {
            "id": str(self.get("_id")),
            "identifier": self.get("identifier"),
            "display_name": self.get("display_name"),
            "description": self.get("description"),
            "open": self.get("open"),
            "signing_public_key": self.get("signing_public_key"),
            "creator": self.get("creator"),
            "created_at": self.get("created_at"),
            "updated_at": self.get("updated_at"),
        }
