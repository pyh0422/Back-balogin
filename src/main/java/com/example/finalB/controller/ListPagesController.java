package com.example.finalB.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.finalB.domain.InTrans;
import com.example.finalB.domain.Trans;
import com.example.finalB.domain.TransType;
import com.example.finalB.service.InTransactionService;
import com.example.finalB.service.TransService;

@RestController
public class ListPagesController {

	@Autowired
	private TransService transService;
	
	@Autowired 
	private InTransactionService inTransactionService;
	
	// listPage 내가 등록한 물품
	@GetMapping("/listPages1/{username}")
	public List<Trans> sellList(@PathVariable String username) {
		
		List<Trans> userSellList = transService.userTransList(username);
		
		return userSellList;
	}
	
	// listPage 판매중
	@GetMapping("/listPages2/{username}")
	public List<Trans> sellingList(@PathVariable String username) {
		
		List<Trans> userSellingList = transService.transINGList(username);
		
		return userSellingList;
		
	}
	
	// listPage 판매완료
	@GetMapping("/listPages3/{username}")
	public List<Trans> selldoneList(@PathVariable String username) {
		
		List<Trans> userSelldoneList = transService.transDONEList(username);
		
		return userSelldoneList;
	}
	
	// listPage 구매중 탭
	@GetMapping("/listPages4/{username}")
	public ResponseEntity<?> BuyList(@PathVariable String username) {
		
		// username으로 거래중인 게시물 intrans를 List로 가져옴
		List<InTrans> intrans = inTransactionService.getIntransBuyInfo(username);
	
		// List로 뽑은 intrans에서 게시물 번호들을 가져옴
	    List<Integer> postIds = intrans.stream().map(InTrans::getPostId).collect(Collectors.toList());
	    
	    // 뽑은 게시물 번호들로 해당 게시물들 목록을 가져옴
//	    List<Trans> transList = transService.getTransByPostIds(postIds);
	    
	    List<Trans> transList = transService.getInTransByPostIds(postIds);
	    
	    return new ResponseEntity<>(transList, HttpStatus.OK);
		
	}
	
	@GetMapping("/listPages5/{username}")
	public ResponseEntity<?> BuyOverList(@PathVariable String username) {
		
		// username으로 거래중인 게시물 intrans를 List로 가져옴
		List<InTrans> intrans = inTransactionService.getIntransBuyInfo(username);
	
		// List로 뽑은 intrans에서 게시물 번호들을 가져옴
	    List<Integer> postIds = intrans.stream().map(InTrans::getPostId).collect(Collectors.toList());
	    
	    List<Trans> transList = transService.getDoneTransByPostIds(postIds);
	    
	    return new ResponseEntity<>(transList, HttpStatus.OK);
		
	}
	
	// mypage 거래현황
	@GetMapping("/mypage/{username}")
	public ResponseEntity<?> mypage(@PathVariable String username) {
		
		Map<String, Long> mypageList = new HashMap<>();
		
		List<InTrans> buying = inTransactionService.getIntransBuyInfo(username);
		
		long ingCount = buying.stream().filter(trans -> trans.getTrans() == TransType.ING).count();
		long doneCount = buying.stream().filter(trans -> trans.getTrans() == TransType.DONE).count();
		
		mypageList.put("ready", transService.countList(username, TransType.READY));
		mypageList.put("ing", transService.countList(username, TransType.ING));
		mypageList.put("done", transService.countList(username, TransType.DONE));
		mypageList.put("buying", ingCount); // 구매중 개수
		mypageList.put("buyDone", doneCount); // 구매완료 개수
		
	
		return new ResponseEntity<>(mypageList, HttpStatus.OK);
		
		
	}
	
}
