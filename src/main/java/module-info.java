module se.uu.ub.cora.gatekeepertokenprovider {
	requires transitive se.uu.ub.cora.httphandler;
	requires se.uu.ub.cora.json;
	requires se.uu.ub.cora.logger;

	exports se.uu.ub.cora.gatekeepertokenprovider;
	exports se.uu.ub.cora.gatekeepertokenprovider.authentication;
}