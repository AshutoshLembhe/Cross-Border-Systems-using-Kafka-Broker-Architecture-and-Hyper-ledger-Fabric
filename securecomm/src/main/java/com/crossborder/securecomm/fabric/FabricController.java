package com.crossborder.securecomm.fabric;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fabric")
public class FabricController {
	private final FabricService fabricService;
	
	public FabricController(FabricService fabricService) {
		this.fabricService=fabricService;
	}
	
	@GetMapping("/get_asset")
	public String getAssets() throws Exception{
		return fabricService.getAllAssets();
	}
	
	@PostMapping("/post_asset")
	public void createAsset(@RequestParam String id,
	                        @RequestParam String color,
	                        @RequestParam String size,
	                        @RequestParam String owner,
	                        @RequestParam String value) throws Exception {
	    fabricService.createAsset(id, color, size, owner, value);
	}
}
