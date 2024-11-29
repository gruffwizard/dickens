package dev.gruff.dickens.ui;

import dev.gruff.dickens.Dickens;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import java.util.ArrayList;
import java.util.List;

@Path("/sketches")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SketchResource {


    @Inject
    Dickens dickens;


    private static final Logger LOG = Logger.getLogger(SketchResource.class);

    private static final List<Sketch> sketches = new ArrayList<>();

    // GET: Fetch all sketches
    @GET
    public List<Sketch> getSketches() {
        return sketches;
    }

    // POST: Add a new sketch
    @POST
    public Response postSketch(Sketch sketch) {
        LOG.info("dickens="+dickens);
        LOG.info("sketching="+sketch.getContent());

        if (sketch.getContent() == null || sketch.getContent().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Sketch content cannot be empty").build();
        }
        sketches.add(sketch);
        dickens.post(sketch.getContent());
        return Response.status(Response.Status.CREATED).build();
    }

    // DELETE: Clear all sketches
    @DELETE
    public Response clearSketches() {
        sketches.clear();
        return Response.ok().build();
    }

    public static class Sketch {
        private String user;
        private String content;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
