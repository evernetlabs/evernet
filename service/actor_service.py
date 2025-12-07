import bcrypt
from mongita.collection import Collection

from service.node_service import NodeService
from utils.time_utils import current_datetime


class ActorService:
    def __init__(self, mongo: Collection, node_service: NodeService) -> None:
        self.mongo = mongo
        self.node_service = node_service

    def sign_up(self, node_identifier: str, identifier: str, password: str, actor_type: str, display_name: str, description: str) -> dict:
        node = self.node_service.get(node_identifier)
        if not node.get('open'):
            raise Exception(f"You are not allowed to sign up on node {node_identifier}")

        
        if self.mongo.count_documents({
            "node_identifier": node_identifier,
            "identifier": identifier
        }) > 0:
            raise Exception(f"Actor {identifier} already exists on node {node_identifier}")

        self.mongo.insert_one({
            "node_identifier": node_identifier,
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8'),
            "type": actor_type,
            "display_name": display_name,
            "description": description,
            "creator": None,
            "created_at": current_datetime(),
            "updated_at": current_datetime()
        })

        return {
            "node_identifier": node_identifier,
            "identifier": identifier
        }

    def get_token(self):
        pass

    def get(self):
        pass

    def update(self):
        pass
    
    def change_password(self):
        pass

    def delete(self):
        pass

    def add(self):
        pass

    def fetch(self):
        pass

    def reset_password(self):
        pass

    @staticmethod
    def to_dict(self):
        pass
