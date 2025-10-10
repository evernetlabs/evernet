import flask

from actor.actor_service import ActorService
from utils.api_utils import required_param, optional_param, authenticate_actor


class ActorAPI:
    def __init__(self, app: flask.Flask, actor_service: ActorService):
        self.app = app
        self.actor_service = actor_service

    def register(self):

        @self.app.post('/api/v1/nodes/<node_identifier>/actors/signup')
        def actor_sign_up(node_identifier: str):
            return self.actor_service.sign_up(
                node_identifier,
                required_param('identifier'),
                required_param('password'),
                required_param('type'),
                required_param('display_name'),
                optional_param('description')
            )

        @self.app.post('/api/v1/nodes/<node_identifier>/actors/token')
        def get_actor_token(node_identifier: str):
            return self.actor_service.get_token(
                node_identifier,
                required_param('identifier'),
                required_param('password'),
                optional_param('target_node_address'),
            )

        @self.app.get('/api/v1/actors/current')
        @authenticate_actor(should_be_local=True)
        def get_current_actor(actor):
            return self.actor_service.get(
                actor['target_node_identifier'],
                actor['identifier'],
            )
