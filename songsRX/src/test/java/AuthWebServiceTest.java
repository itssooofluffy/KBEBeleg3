import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import de.htwBerlin.ai.kbe.services.Auth;

public class AuthWebServiceTest extends JerseyTest {

	@Override
	protected Application configure() {
                return new ResourceConfig(Auth.class);
	}
		
        @Test
        public void validUserIdToAuthShouldReturn200() {
            Response response = target("/auth").queryParam("userId", "mmuster").request().get();
            System.out.println("Valid UserId Request: " + response.getStatus());
            Assert.assertEquals(200, response.getStatus());
        }
        
        @Test
        public void NoValidUserIdToAuthShouldReturn403() {
            Response response = target("/auth").queryParam("userId", "muster").request().get();
            System.out.println("Non Valid UserId Request: " + response.getStatus());
            Assert.assertEquals(403, response.getStatus());
        }
        
         @Test
        public void NoUserIdToAuthShouldReturn403() {
            Response response = target("/auth").request().get();
            System.out.println("Non Valid UserId Request: " + response.getStatus());
            Assert.assertEquals(403, response.getStatus());
        }
	
}
