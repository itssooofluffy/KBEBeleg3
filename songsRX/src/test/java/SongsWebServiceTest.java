import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import de.htwBerlin.ai.kbe.bean.Song;
import de.htwBerlin.ai.kbe.services.Auth;
import de.htwBerlin.ai.kbe.services.Songs;

public class SongsWebServiceTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(Songs.class);
	}
	
        @Test
	public void updateSongWithNonExistingIDShouldReturn404 () {
		Song mySong = new Song();
		mySong.setId(14);
		mySong.setArtist("Test");
		mySong.setTitle("TestTitle");
		mySong.setAlbum("TestAlbum");
		mySong.setReleased(2016);
		Response response = target("/songs/14").request().header("authorization", "testToken").put(Entity.json(mySong));
		System.out.println("Update with no provided ID: " + response.getStatus());
		Assert.assertEquals(404, response.getStatus());
	}
	
	@Test
	public void updateSongWithExistingIdShouldReturn204 () {
		Song mySong = new Song();
		mySong.setId(10);
		mySong.setArtist("Test");
		mySong.setTitle("TestTitle");
		mySong.setAlbum("TestAlbum");
		mySong.setReleased(2016);
		Response response = target("/songs/10").request().header("authorization", "testToken").put(Entity.xml(mySong));
		System.out.println("Update with matching ID: " + response.getStatus());
		Assert.assertEquals(204, response.getStatus());
	}
	
	@Test
	public void updateSongWithNonMatchingIdShouldReturn400 () {
		Song mySong = new Song();
		mySong.setId(10);
		mySong.setArtist("Test");
		mySong.setTitle("TestTitle");
		mySong.setAlbum("TestAlbum");
		mySong.setReleased(2016);
		Response response = target("/songs/14").request().header("authorization", "testToken").put(Entity.xml(mySong));
		System.out.println("Update with non matching ID: " + response.getStatus());
		Assert.assertEquals(400, response.getStatus());
	}	
}
