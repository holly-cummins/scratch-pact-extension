package sample.house;

import au.com.dius.pact.core.support.expressions.ValueResolver;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junitsupport.loader.PactFolderLoader;
import sample.resident.Resident;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Path("/resident")
public class ResidentResource {

    class DebugAnnotation implements PactFolder {
        @Override
        public Class<? extends ValueResolver> valueResolver() {
            return null;
        }

        @Override
        public String value() {
            return null;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return null;
        }
    }

    // This is a non-cloud-safe way of handling session affinity.
    // For simplicity, it will do!
    private static final Map<String, Resident> residents = new HashMap<>();

    private Resident createResident(String sessionId) {
        final Resident abby = new Resident();
        residents.put(sessionId, abby);
        // Why don't these blow up since I should have removed these classes?
        System.out.println("HOLLY resident sees " + PactVerificationContext.class);
        System.out.println("HOLLY resident sees " + PactFolderLoader.class.getProtectionDomain());
        System.out.println("HOLLY resident sees " + new PactFolderLoader((PactFolder) new Bla()).getPactSource());
        wakeResident(abby);
        return abby;
    }

    private void wakeResident(Resident abby) {
        abby.setRoom("bedroom");
//        pause();
//        abby.setRoom("bathroom");
//        abby = new Bathroom().visit(abby);
//        pause();
//        abby.setRoom("kitchen");
//        abby = new Kitchen().visit(abby);
//        pause();
//        abby.setRoom("bedroom");
//        abby = new Bedroom().visit(abby);

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Resident getResident(@CookieParam("resident-name") String residentName) {
        return Objects.requireNonNullElseGet(residents.get(residentName), () -> createResident(residentName));
    }

    /*
     * Allow state to be visualised better by leaving pauses between rooms.
     */
    private void pause() {
//        try {
//     //       Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

}
