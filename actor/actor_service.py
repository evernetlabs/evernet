import time
import uuid

import bcrypt
import jwt
from pymongo.collection import Collection

from config.config_service import ConfigService
from node.node_service import NodeService
from utils.ed25519_utils import string_to_private_key
from utils.time_utils import current_datetime


class ActorService:
    def __init__(self, mongo: Collection, node_service: NodeService, config_service: ConfigService):
        self.mongo = mongo
        self.node_service = node_service
        self.config_service = config_service

    def sign_up(self, node_identifier: str, identifier: str, password: str, type: str, display_name: str, description: str):
        node = self.node_service.get(node_identifier)

        if not node.get('open'):
            raise Exception('You are not allowed to perform this action')

        if self.mongo.count_documents({
            'node_identifier': node_identifier,
            'identifier': identifier,
        }) > 0:
            raise Exception(f'Actor {identifier} already exists on node {node_identifier}')

        self.mongo.insert_one({
            'node_identifier': node_identifier,
            'identifier': identifier,
            'password': bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            "type": type,
            'display_name': display_name,
            'description': description,
            'creator': None,
            'created_at': current_datetime(),
            'updated_at': current_datetime(),
        })

        return {
            'node_identifier': node_identifier,
            'identifier': identifier,
        }

    def get_token(self, node_identifier: str, identifier: str, password: str, target_node_address: str = None, ttl: int = 24 * 3600) -> dict:
        node = self.node_service.get_signing_private_key(node_identifier)
        signing_private_key = string_to_private_key(node.get('signing_private_key'))

        actor = self.mongo.find_one({
            'node_identifier': node_identifier,
            'identifier': identifier,
        })

        if not actor or not bcrypt.checkpw(password.encode(), actor.get('password').encode()):
            raise Exception('Invalid identifier and password combination')

        vertex_endpoint = self.config_service.get_vertex_endpoint()

        if not target_node_address:
            target_node_address = f'{vertex_endpoint}/{node_identifier}'

        token = jwt.encode(
            {
                'type': 'actor',
                'sub': actor.get('identifier'),
                'iss': f'{vertex_endpoint}/{node_identifier}',
                'aud': target_node_address,
                'iat': int(time.time()),
                'exp': int(time.time()) + ttl,
                'jti': str(uuid.uuid4()),
            },
            headers={
                'kid': f'{vertex_endpoint}/{node_identifier}',
            },
            key=signing_private_key,
            algorithm='EdDSA',
        )

        return {
            'token': token
        }

    def get(self, node_identifier: str, identifier: str) -> dict:
        actor = self.mongo.find_one({
            'node_identifier': node_identifier,
            'identifier': identifier,
        })

        if not actor:
            raise Exception(f'Actor {identifier} not found on node {node_identifier}')

        return self.to_dict(actor)

    def add(self):
        pass

    @staticmethod
    def to_dict(self):
        return {
            'id': str(self.get('_id')),
            'node_identifier': self.get('node_identifier'),
            'identifier': self.get('identifier'),
            'type': self.get('type'),
            'display_name': self.get('display_name'),
            'description': self.get('description'),
            'creator': self.get('creator'),
            'created_at': self.get('created_at'),
            'updated_at': self.get('updated_at'),
        }