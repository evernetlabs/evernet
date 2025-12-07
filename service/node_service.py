from mongita.collection import Collection
from utils.ed25519_utils import generate_ed25519_keys, private_key_to_string, public_key_to_string
from utils.time_utils import current_datetime


class NodeService:
    def __init__(self, mongo: Collection) -> None:
        self.mongo = mongo
    
    def create(self, identifier: str, display_name: str, description: str, creator: str) -> dict:
        if self.mongo.count_documents({
            "identifier": identifier
        }) > 0:
            raise Exception(f"Node {identifier} already exists")

        private_key, public_key = generate_ed25519_keys()
        
        self.mongo.insert_one({
            "identifier": identifier,
            "display_name": display_name,
            "description": description,
            "private_key": private_key_to_string(private_key),
            "public_key": public_key_to_string(public_key),
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime()
        })

        return {
            "identifier": identifier
        }
    
    def fetch(self) -> list[dict]:
        pass
    
    def get(self, identifier: str) -> dict:
        pass
        
    def update(self, identifier: str, display_name: str, description: str) -> dict:
        pass
    
    def delete(self, identifier: str) -> dict:
        pass
    
    @staticmethod
    def to_dict(self) -> dict:
        pass