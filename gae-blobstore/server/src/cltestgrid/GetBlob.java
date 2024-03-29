/* Copyright (c) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cltestgrid;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;

@SuppressWarnings("serial")
public class GetBlob extends HttpServlet {

  private BlobstoreService blobstoreService =
    BlobstoreServiceFactory.getBlobstoreService();

  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {

    final String key = req.getParameter("key");

    BlobKey blobKey;
    // the key may be specified in one of two forms:
    if (key.length() > 19) {
        // Very long string (162 characters) are real blobstore BlobKeys;
        // the URLs with such long keys are very nasty.
        blobKey = new BlobKey(key);
    } else {
        // Indirectly, via intermediate short key,
        // which is ID of a datastore Entity, storing 
        // the original BlobKey; 
        // that way we allow prettier URLs.
        Key datastoreKey = KeyFactory.createKey("ShortKey", Long.valueOf(key));
        try {
            Entity shortKeyEntity = datastore.get(datastoreKey);
            blobKey = (BlobKey)shortKeyEntity.getProperty("blobKey");
        } catch (EntityNotFoundException e) {
            throw new ServletException("Unknown key is specified: " + key);
        }
    }

    blobstoreService.serve(blobKey, resp);
  }
}
