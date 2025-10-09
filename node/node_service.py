from pymongo.collection import Collection

from utils.ed25519_utils import generate_ed25519_keys, private_key_to_string, public_key_to_string
from utils.time_utils import current_datetime


class NodeService:
    def __init__(self, mongo: Collection):
        self.mongo = mongo

    def create(self, identifier: str, display_name: str, description: str, open: bool, creator: str) -> dict:
        if self.mongo.count_documents({
            'identifier': identifier
        }) > 0:
            raise Exception(f'Node {identifier} already exists')

        signing_private_key, signing_public_key = generate_ed25519_keys()

        self.mongo.insert_one({
            'identifier': identifier,
            'display_name': display_name,
            'description': description,
            'open': open,
            'signing_private_key': private_key_to_string(signing_private_key),
            'signing_public_key': public_key_to_string(signing_public_key),
            'creator': creator,
            'created_at': current_datetime(),
            'updated_at': current_datetime(),
        })

        return {
            'identifier': identifier
        }

    def fetch(self, page: int = 0, size: int = 50) -> list[dict]:
        nodes = self.mongo.find({}).skip(page * size).limit(size)

        result = []

        for node in nodes:
            result.append(self.to_dict(node))

        return result

    def fetch_open(self):
        pass

    def get(self):
        pass

    def update(self):
        pass

    def update_open(self):
        pass

    def reset_signing_keys(self):
        pass

    def delete(self):
        pass

    @staticmethod
    def to_dict(self):
        return {
            'id': str(self.get('_id')),
            'identifier': self.get('identifier'),
            'display_name': self.get('display_name'),
            'description': self.get('description'),
            'open': self.get('open'),
            'signing_public_key': self.get('signing_public_key'),
            'creator': self.get('creator'),
            'created_at': self.get('created_at'),
            'updated_at': self.get('updated_at'),
        }
