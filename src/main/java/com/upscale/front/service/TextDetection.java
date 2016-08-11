package com.upscale.front.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.collect.ImmutableList;

@Service
@Transactional
public class TextDetection {
	
	private final Logger log = LoggerFactory.getLogger(TextDetection.class);
	
	private static final String APPLICATION_NAME = "Upscale-Finocial/1.0";
	
	public static Vision getVisionService() throws IOException, GeneralSecurityException {
		GoogleCredential credential = GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}
	
	private final Vision vision;
	
	/**
	 * Constructs a {@link TextDetection} which connects to the vision api.
	 * @param vision
	 */
	public TextDetection(){
		this.vision = null;
	}
	
	public TextDetection(Vision vision) {
		this.vision = vision;
	}

	/**
	 * Gets up to {@code maxResults} texts for an image stored at {@code byte[]}.
	 */
	
	public List<EntityAnnotation> detectText(byte[] data, int maxResults) throws IOException {
		
		AnnotateImageRequest request =
				new AnnotateImageRequest()
					.setImage(new Image().encodeContent(data))
					.setFeatures(ImmutableList.of(
							new Feature()
								.setType("TEXT_DETECTION")
								.setMaxResults(maxResults)));
		
		Vision.Images.Annotate annotate =
				vision.images()
					.annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
		annotate.setDisableGZipContent(true);
		
		BatchAnnotateImagesResponse batchResponse = annotate.execute();
		assert batchResponse.getResponses().size() == 1;
		AnnotateImageResponse response = batchResponse.getResponses().get(0);
		if(response.getTextAnnotations() == null) {
			throw new IOException(
					response.getError() != null
						? response.getError().getMessage()
						: "Unknown error getting image annotations");
		}
		return response.getTextAnnotations();
	}
}
