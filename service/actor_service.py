import time
import uuid
import bcrypt
import jwt
from mongita.collection import Collection

from service.config_service import ConfigService
from service.node_service import NodeService
from utils.ed25519_utils import string_to_private_key
from utils.time_utils import current_datetime


class ActorService:
    def __init__(self, mongo: Collection, node_service: NodeService, config_service: ConfigService) -> None:
        self.mongo = mongo
        self.node_service = node_service
        self.config_service = config_service

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

    def get_token(self, node_identifier: str, identifier: str, password: str, audience_node_address: str) -> dict:
        node = self.node_service.get_signing_keys(node_identifier)

        actor = self.mongo.find_one({
            "node_identifier": node_identifier,
            "identifier": identifier
        })

        if not actor or not bcrypt.checkpw(password.encode('utf-8'), actor.get('password').encode('utf-8')):
            raise Exception("Invalid identifier and password combination")

        issuer_node_address = f"{self.config_service.get_vertex_endpoint()}/{node_identifier}"
        if not audience_node_address:
            audience_node_address = issuer_node_address
        else:
            if len(audience_node_address.split("/")) != 2:
                raise Exception(f"Invalid audience node address {audience_node_address}")

        return {
            "token": jwt.encode({
                "sub": actor.get("identifier"),
                "type": "actor",
                "jti": str(uuid.uuid4()),
                "iat": int(time.time()),
                "exp": int(time.time()) + 30 * 24 * 60 * 60,
                "iss": issuer_node_address,
                "aud": audience_node_address
            }, headers={
                "kid": issuer_node_address
            }, key=string_to_private_key(node.get("signing_private_key")), algorithm="EdDSA")
        }

    def get(self, node_identifier: str, identifier: str) -> dict:
        actor = self.mongo.find_one({
            "node_identifier": node_identifier,
            "identifier": identifier
        })
        
        if not actor:
            raise Exception(f"Actor {identifier} not found on node {node_identifier}")

        return self.to_dict(actor)

    def update(self, node_identifier: str, identifier: str, actor_type: str, display_name: str, description: str) -> dict:
        fields = {
            "updated_at": current_datetime()
        }
        
        if actor_type:
            fields["type"] = actor_type
        if display_name:
            fields["display_name"] = display_name
        if description:
            fields["description"] = description
        
        result = self.mongo.update_one({
            "node_identifier": node_identifier,
            "identifier": identifier
        }, {
            "$set": fields
        })

        if result.matched_count == 0:
            raise Exception(f"Actor {identifier} not found on node {node_identifier}")

        return {
            "identifier": identifier,
            "node_identifier": node_identifier
        }
    
    def change_password(self, node_identifier: str, identifier: str, password: str) -> dict:
        result = self.mongo.update_one({
            "node_identifier": node_identifier,
            "identifier": identifier
        }, {
            "$set": {
                "password": bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8'),
                "updated_at": current_datetime()
            }
        })
        
        if result.matched_count == 0:
            raise Exception(f"Actor {identifier} not found on node {node_identifier}")

        return {
            "identifier": identifier,
            "node_identifier": node_identifier
        }

    def delete(self, node_identifier: str, identifier: str) -> dict:
        result = self.mongo.delete_one({
            "node_identifier": node_identifier,
            "identifier": identifier
        })
        
        if result.deleted_count == 0:
            raise Exception(f"Actor {identifier} not found on node {node_identifier}")

        return {
            "identifier": identifier,
            "node_identifier": node_identifier
        }

    def add(self, node_identifier: str, identifier: str, actor_type: str, display_name: str, description: str, creator: str) -> dict:
        pass

    def fetch(self):
        pass

    def reset_password(self):
        pass

    @staticmethod
    def to_dict(actor: dict):
        return {
            "node_identifier": actor.get("node_identifier"),
            "identifier": actor.get("identifier"),
            "type": actor.get("type"),
            "display_name": actor.get("display_name"),
            "description": actor.get("description"),
            "creator": actor.get("creator"),
            "created_at": actor.get("created_at"),
            "updated_at": actor.get("updated_at")
        }
