from flask import Flask, request

from service.state_service import StateService
from utils.api import required_param, optional_param
from utils.auth import authenticate_admin


class StateAPI:
    def __init__(self, app: Flask, state_service: StateService):
        self.app = app
        self.state_service = state_service

    def register(self):

        @self.app.post("/api/v1/admins/nodes/<node_identifier>/structure/states")
        @authenticate_admin
        def create_state(admin, node_identifier):
            return self.state_service.create(
                node_identifier,
                request.args.get("structure_address"),
                required_param("identifier"),
                required_param("display_name"),
                optional_param("description"),
                admin.get("identifier")
            )

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/structure/states")
        @authenticate_admin
        def fetch_states(_, node_identifier):
            return self.state_service.fetch(node_identifier, request.args.get("structure_address"))

        @self.app.get("/api/v1/admins/nodes/<node_identifier>/structure/state")
        @authenticate_admin
        def get_state(_, node_identifier):
            return self.state_service.get(
                node_identifier,
                request.args.get("structure_address"),
                request.args.get("identifier")
            )

        @self.app.put("/api/v1/admins/nodes/<node_identifier>/structure/state")
        @authenticate_admin
        def update_state(_, node_identifier):
            return self.state_service.update(
                node_identifier,
                request.args.get("structure_address"),
                request.args.get("identifier"),
                optional_param("display_name"),
                optional_param("description")
            )

        @self.app.delete("/api/v1/admins/nodes/<node_identifier>/structure/state")
        @authenticate_admin
        def delete_state(_, node_identifier):
            return self.state_service.delete(
                node_identifier,
                request.args.get("structure_address"),
                request.args.get("identifier")
            )
