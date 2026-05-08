from cryptography.hazmat.primitives.asymmetric import ed25519
from pymongo.collection import Collection

from util.key import generate_ed25519_keys, ed25519_private_key_to_string, ed25519_public_key_to_string, \
    ed25519_string_to_private_key
from util.time import current_datetime


class NodeService:
    def __init__(self, mongo: Collection):
        self.mongo = mongo

    def create(self, identifier: str, display_name: str, description: str, open: bool, creator: str) -> dict:
        if self.mongo.count_documents({
            "identifier": identifier,
        }) > 0:
            raise Exception(f"Node {identifier} already exists")

        signing_private_key, signing_public_key = generate_ed25519_keys()

        self.mongo.insert_one({
            "identifier": identifier,
            "display_name": display_name,
            "description": description,
            "open": open,
            "signing_private_key": ed25519_private_key_to_string(signing_private_key),
            "signing_public_key": ed25519_public_key_to_string(signing_public_key),
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime(),
        })

        return {
            "identifier": identifier,
        }

    def fetch(self, page: int = 0, size: int = 50) -> list[dict]:
        nodes = self.mongo.find({
            "open": True,
        }).skip(page * size).limit(size)

        return [self.to_dict(node) for node in nodes]

    def fetch_all(self, page: int = 0, size: int = 50) -> list[dict]:
        nodes = self.mongo.find({}).skip(page * size).limit(size)
        return [self.to_dict(node) for node in nodes]

    def get(self, identifier: str) -> dict:
        node = self.mongo.find_one({"identifier": identifier})
        if not node:
            raise Exception(f"Node {identifier} not found")
        return self.to_dict(node)

    def update(self, identifier: str, display_name: str, description: str) -> dict:
        fields = {
            "description": description,
            "updated_at": current_datetime(),
        }

        if display_name:
            fields["display_name"] = display_name

        result = self.mongo.update_one({
            "identifier": identifier,
        }, {
            "$set": fields,
        })

        if result.matched_count == 0:
            raise Exception(f"Node {identifier} not found")

        return {
            "identifier": identifier,
        }

    def update_open(self, identifier: str, open: bool) -> dict:
        result = self.mongo.update_one({
            "identifier": identifier,
        }, {
            "$set": {
                "open": open,
                "updated_at": current_datetime(),
            }
        })

        if result.matched_count == 0:
            raise Exception(f"Node {identifier} not found")

        return {
            "identifier": identifier,
        }

    def delete(self, identifier: str) -> dict:
        result = self.mongo.delete_one({
            "identifier": identifier,
        })

        if result.deleted_count == 0:
            raise Exception(f"Node {identifier} not found")

        return {
            "identifier": identifier,
        }

    def reset_signing_key(self, identifier: str) -> dict:
        signing_private_key, signing_public_key = generate_ed25519_keys()
        result = self.mongo.update_one({
            "identifier": identifier,
        }, {
            "$set": {
                "signing_private_key": ed25519_private_key_to_string(signing_private_key),
                "signing_public_key": ed25519_public_key_to_string(signing_public_key),
                "updated_at": current_datetime(),
            }
        })

        if result.matched_count == 0:
            raise Exception(f"Node {identifier} not found")

        return {
            "identifier": identifier,
            "signing_public_key": ed25519_public_key_to_string(signing_public_key),
        }

    def get_signing_private_key(self, identifier: str) -> ed25519.Ed25519PrivateKey:
        node = self.mongo.find_one({"identifier": identifier})

        if not node:
            raise Exception(f"Node {identifier} not found")

        return ed25519_string_to_private_key(node["signing_private_key"])

    def exists(self, identifier: str) -> bool:
        return self.mongo.count_documents({"identifier": identifier}) > 0

    @staticmethod
    def to_dict(self):
        return {
            "identifier": self["identifier"],
            "display_name": self["display_name"],
            "description": self["description"],
            "open": self["open"],
            "signing_public_key": self["signing_public_key"],
            "creator": self["creator"],
            "created_at": self["created_at"],
            "updated_at": self["updated_at"],
        }