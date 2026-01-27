import time
import uuid

import bcrypt
import jwt
from pymongo.collection import Collection

from exception.types import AuthorizationException, ClientException, NotFoundException
from service.config_service import ConfigService
from service.node_service import NodeService
from utils.secret import generate_secret
from utils.time import current_datetime


class ActorService:
    def __init__(self, mongo: Collection, node_service: NodeService, config_service: ConfigService) -> None:
        self.mongo = mongo
        self.node_service = node_service
        self.config_service = config_service

    def sign_up(self, node_identifier: str, identifier: str, password: str, display_name: str, description: str, actor_type: str) -> dict:
        node = self.node_service.get(node_identifier)
        if not node.get("open"):
            raise AuthorizationException(f"You are not allowed to sign up on node {node_identifier}")

        if self.mongo.count_documents({
            "node_identifier": node_identifier,
            "identifier": identifier
        }) > 0:
            raise ClientException(f"Actor with identifier {identifier} already exists on node {node_identifier}")

        self.mongo.insert_one({
            "node_identifier": node_identifier,
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt(12)).decode("utf-8"),
            "display_name": display_name,
            "description": description,
            "type": actor_type,
            "creator": None,
            "created_at": current_datetime(),
            "updated_at": current_datetime()
        })

        return {
            "identifier": identifier
        }

    def get_token(self, node_identifier: str, identifier: str, password: str, audience_node_address: str) -> dict:
        actor = self.mongo.find_one({"node_identifier": node_identifier, "identifier": identifier})

        if not actor or not bcrypt.checkpw(password.encode("utf-8"), actor.get("password").encode("utf-8")):
            raise AuthorizationException("Invalid login credentials")

        current_vertex_endpoint = self.config_service.get_vertex_endpoint()

        if not audience_node_address:
            audience_node_address = f"{current_vertex_endpoint}/{node_identifier}"

        issuer = f"{current_vertex_endpoint}/{node_identifier}"

        token = jwt.encode({
            "sub": identifier,
            "iss": issuer,
            "aud": audience_node_address,
            "type": "actor",
            "iat": int(time.time()),
            "exp": int(time.time()) + 3600 * 24 * 7,
            "jti": str(uuid.uuid4())
        }, key=self.node_service.get_signing_private_key(node_identifier), algorithm="EdDSA", headers={"kid": issuer})

        return {
            "token": token
        }

    def get(self, node_identifier: str, identifier: str) -> dict:
        actor = self.mongo.find_one({"node_identifier": node_identifier, "identifier": identifier})

        if not actor:
            raise ClientException(f"Actor with identifier {identifier} not found on node {node_identifier}")

        return self.to_dict(actor)

    def update(self, node_identifier: str, identifier: str, display_name: str, description: str, actor_type: str) -> dict:
        fields = {
            "updated_at": current_datetime(),
            "description": description,
        }

        if display_name:
            fields["display_name"] = display_name

        if actor_type:
            fields["type"] = actor_type

        result = self.mongo.update_one({
            "node_identifier": node_identifier,
            "identifier": identifier
        }, {
            "$set": fields
        })

        if result.matched_count == 0:
            raise NotFoundException(f"Actor with identifier {identifier} not found on node {node_identifier}")

        return {
            "identifier": identifier
        }

    def change_password(self, node_identifier: str, identifier: str, password: str) -> dict:
        fields = {
            "updated_at": current_datetime,
            "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt(12)).decode("utf-8"),
        }

        result = self.mongo.update_one({
            "node_identifier": node_identifier,
            "identifier": identifier
        }, {
            "$set": fields
        })

        if result.matched_count == 0:
            raise NotFoundException("Actor with identifier {identifier} not found on node {node_identifier}")

        return {
            "identifier": identifier
        }

    def delete(self, node_identifier: str, identifier: str) -> dict:
        result = self.mongo.delete_one({
            "node_identifier": node_identifier,
            "identifier": identifier
        })

        if result.deleted_count == 0:
            raise NotFoundException(f"Actor with identifier {identifier} not found on node {node_identifier}")

        return {
            "identifier": identifier
        }

    def add(self, node_identifier: str, identifier: str, display_name: str, description: str, actor_type: str, creator: str):
        if not self.node_service.exists(node_identifier):
            raise Exception(f"Node {node_identifier} not found")

        if self.mongo.count_documents({
            "node_identifier": node_identifier,
            "identifier": identifier
        }) > 0:
            raise Exception(f"Actor with identifier {identifier} already exists on node {node_identifier}")

        password = generate_secret(16)

        self.mongo.insert_one({
            "node_identifier": node_identifier,
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt(12)).decode("utf-8"),
            "display_name": display_name,
            "description": description,
            "type": actor_type,
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime()
        })

        return {
            "identifier": identifier,
            "password": password
        }

    def fetch(self, node_identifier: str, page: int = 0, size: int = 50) -> list[dict]:
        actors = self.mongo.find({
            "node_identifier": node_identifier
        }).skip(page*size).limit(size)

        return [self.to_dict(actor) for actor in actors]

    def reset_password(self, node_identifier: str, identifier: str) -> dict:
        password = generate_secret(16)
        result = self.change_password(node_identifier, identifier, password)
        result["password"] = password
        return result

    @staticmethod
    def to_dict(self) -> dict:
        return {
            "node_identifier": self.get("node_identifier"),
            "identifier": self.get("identifier"),
            "display_name": self.get("display_name"),
            "description": self.get("description"),
            "type": self.get("type"),
            "creator": self.get("creator"),
            "created_at": self.get("created_at"),
            "updated_at": self.get("updated_at")
        }