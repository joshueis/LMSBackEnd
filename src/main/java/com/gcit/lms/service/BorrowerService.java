package com.gcit.lms.service;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:50667")
public class BorrowerService {
	
//	@Autowired
//	BorrowerDAO bwdao;
//	
//	@RequestMapping(value = "/borrowers/{cardNo}", method = RequestMethod.GET)
//	public Borrower getBorrower(@PathVariable Integer cardNo) throws SQLException {
//		Borrower b = bwdao.readBorrowersByPK(cardNo);
//		return b;
//		if(b == null){
//			return new ResponseEntity<Borrower>(HttpStatus.NOT_FOUND);
//		}
//		return new ResponseEntity<Borrower>(b, HttpStatus.OK);
//	}
//	
	
	
}
