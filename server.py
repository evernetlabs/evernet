import os
from dotenv import load_dotenv
from flask import Flask, g, request
from flask_cors import CORS
from montydb import set_storage, MontyClient
import pymongo

from api.actor_api import ActorAPI
from api.admin_api import AdminAPI
from api.config_api import ConfigAPI
from api.event_api import EventAPI
from api.health_check_api import HealthCheckAPI
from api.node_api import NodeAPI
from api.property_api import PropertyAPI
from api.relationship_api import RelationshipAPI
from api.state_api import StateAPI
from api.structure_api import StructureAPI
from api.vertex_api import VertexAPI
from exception.handler import register_exception_handler
from service.actor_service import ActorService
from service.admin_service import AdminService
from service.config_service import ConfigService
from service.event_service import EventService
from service.node_key_service import NodeKeyService
from service.node_service import NodeService
from service.property_service import PropertyService
from service.relationship_service import RelationshipService
from service.remote_node_service import RemoteNodeService
from service.state_service import StateService
from service.structure_clone_service import StructureCloneService
from service.structure_service import StructureService
from service.vertex_service import VertexService
from web.routes import WebRoutes

load_dotenv()

use_embedded_database = os.getenv("USE_EMBEDDED_DB", "true").lower() == "true"
if use_embedded_database:
    embedded_database_directory = os.getenv("EMBEDDED_DB_DATA_DIR", "data")
    set_storage(embedded_database_directory, storage="lightning")
    client = MontyClient(embedded_database_directory)
    db = client.evernet
else:
    db = pymongo.MongoClient(os.getenv("REMOTE_DB_HOST", "mongodb://localhost:27017")).evernet

app = Flask(__name__)
CORS(app)

config_service = ConfigService(db.configs)
vertex_service = VertexService(config_service)
admin_service = AdminService(db.admins, config_service)
node_service = NodeService(db.nodes)
remote_node_service = RemoteNodeService(config_service)
node_key_service = NodeKeyService(node_service, remote_node_service, config_service)
actor_service = ActorService(db.actors, node_service, config_service)
structure_service = StructureService(db.structures, node_service, config_service)
structure_clone_service = StructureCloneService(structure_service, config_service)
relationship_service = RelationshipService(db.relationships, structure_service, structure_clone_service)
state_service = StateService(db.states, structure_service)
property_service = PropertyService(db.properties, structure_service)
event_service = EventService(db.events, structure_service)

HealthCheckAPI(app).register()
VertexAPI(app, vertex_service).register()
AdminAPI(app, admin_service).register()
ConfigAPI(app, config_service).register()
NodeAPI(app, node_service).register()
ActorAPI(app, actor_service).register()
StructureAPI(app, structure_service).register()
RelationshipAPI(app, relationship_service).register()
StateAPI(app, state_service).register()
PropertyAPI(app, property_service).register()
EventAPI(app, event_service).register()

WebRoutes(app).register()


@app.before_request
def before_request():
    g.request_body = request.get_json(force=True, silent=True)
    g.config_service = config_service
    g.node_service = node_service
    g.node_key_service = node_key_service


register_exception_handler(app)

if __name__ == '__main__':
    app.run(
        host=os.getenv("HOST", "0.0.0.0"),
        port=int(os.getenv("PORT", "8080")),
        debug=os.getenv("ENV", "DEV") != "PROD"
    )
