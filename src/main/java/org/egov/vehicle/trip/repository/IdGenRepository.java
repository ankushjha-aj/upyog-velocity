package org.egov.vehicle.trip.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.egov.vehicle.config.VehicleConfiguration;
import org.egov.vehicle.trip.web.model.idgen.IdGenerationRequest;
import org.egov.vehicle.trip.web.model.idgen.IdGenerationResponse;
import org.egov.vehicle.trip.web.model.idgen.IdRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Repository
public class IdGenRepository {
	private RestTemplate restTemplate;

	private VehicleConfiguration config;

	@Autowired
	public IdGenRepository(RestTemplate restTemplate, VehicleConfiguration config) {
		this.restTemplate = restTemplate;
		this.config = config;
	}

	public IdGenerationResponse getId(RequestInfo requestInfo, String tenantId, String name, String format) {

		List<IdRequest> reqList = new ArrayList<>();
		reqList.add(IdRequest.builder().idName(name).format(format).tenantId(tenantId).build());
		IdGenerationRequest req = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
		IdGenerationResponse response = null;
		try {
			response = restTemplate.postForObject(config.getIdGenHost() + config.getIdGenPath(), req,
					IdGenerationResponse.class);
		} catch (HttpClientErrorException e) {
			throw new ServiceCallException(e.getResponseBodyAsString());
		} catch (Exception e) {
			Map<String, String> map = new HashMap<>();
			map.put(e.getCause().getClass().getName(), e.getMessage());
			throw new CustomException(map);
		}
		return response;
	}
}
