from mongita.collection import Collection
from utils.ed25519_utils import generate_ed25519_keys, private_key_to_string, public_key_to_string
from utils.time_utils import current_datetime


class NodeService:
    def __init__(self, mongo: Collection) -> None:
        self.mongo = mongo
    
    def create(self, identifier: str, display_name: str, description: str, open: bool, creator: str) -> dict:
        if self.mongo.count_documents({
            "identifier": identifier
        }) > 0:
            raise Exception(f"Node {identifier} already exists")

        private_key, public_key = generate_ed25519_keys()
        
        self.mongo.insert_one({
            "identifier": identifier,
            "display_name": display_name,
            "description": description,
            "open": open,
            "signing_private_key": private_key_to_string(private_key),
            "signing_public_key": public_key_to_string(public_key),
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime()
        })

        return {
            "identifier": identifier
        }
    
    def fetch(self, page: int = 0, size: int = 50) -> list[dict]:
        nodes = self.mongo.find().skip(page * size).limit(size)
        return [self.to_dict(node) for node in nodes]

    def fetch_open(self, page: int = 0, size: int = 50) -> list[dict]:
        nodes = self.mongo.find({"open": True}).skip(page * size).limit(size)
        return [self.to_dict(node) for node in nodes]
    
    def get(self, identifier: str) -> dict:
        node = self.mongo.find_one({
            "identifier": identifier
        })

        if not node:
            raise Exception(f"Node {identifier} not found")
        return self.to_dict(node)
        
    def update(self, identifier: str, display_name: str, description: str) -> dict:
        fields = {
            "updated_at": current_datetime()
        }

        if display_name:
            fields["display_name"] = display_name
        if description:
            fields["description"] = description

        result = self.mongo.update_one({
            "identifier": identifier
        }, {
            "$set": fields
        })

        if result.matched_count == 0:
            raise Exception(f"Node {identifier} not found")

        return {
            "identifier": identifier
        }

    def update_open(self, identifier: str, open: bool) -> dict:
        fields = {
            "updated_at": current_datetime(),
            "open": open
        }

        result = self.mongo.update_one({
            "identifier": identifier
        }, {
            "$set": fields
        })

        if result.matched_count == 0:
            raise Exception(f"Node {identifier} not found")

        return {
            "identifier": identifier
        }
    
    def delete(self, identifier: str) -> dict:
        result = self.mongo.delete_one({
            "identifier": identifier
        })

        if result.deleted_count == 0:
            raise Exception(f"Node {identifier} not found")

        return {
            "identifier": identifier
        }
    
    @staticmethod
    def to_dict(node: dict) -> dict:
        return {
            "identifier": node.get("identifier"),
            "display_name": node.get("display_name"),
            "description": node.get("description"),
            "open": node.get("open"),
            "creator": node.get("creator"),
            "created_at": node.get("created_at"),
            "updated_at": node.get("updated_at")
        }
