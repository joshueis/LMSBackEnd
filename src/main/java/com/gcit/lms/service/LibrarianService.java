package com.gcit.lms.service;

import org.springframework.web.bind.annotation.RestController;


@RestController
public class LibrarianService {

//	@Autowired
//	BranchDAO brdao;
//	@Autowired
//	BookDAO bkdao;
//
//	@RequestMapping(value = "/branches", method = RequestMethod.GET)
//	public List<Branch> getBranches() throws SQLException {
//		List<Branch> branches = new ArrayList<>();
//		branches = brdao.readAllBranches();
//		return branches;
//	}
//
//	@RequestMapping(value = "/branches/{branchId}/branchBooks", method = RequestMethod.GET)
//	public List<BranchBook> getBranchBooks(@PathVariable Integer branchId)
//			throws SQLException {
//		List<BranchBook> books = new ArrayList<>();
//		for (Map.Entry<Integer, Integer> entry : brdao
//				.readBranchesByPK(branchId).getBookCopies().entrySet()) {
//			BranchBook bBook = new BranchBook();
//			Integer key = entry.getKey();
//			Integer value = entry.getValue();
//			bBook.setBookId(key);
//			bBook.setTitle(bkdao.readBooksByPK(key).getTitle());
//			bBook.setNoOfCopies(value);
//			bBook.setBranchId(branchId);
//			books.add(bBook);
//		}
//		return books;
//	}
//
//	//TODO: Modify by adding BranchBookDAO
//	@RequestMapping(value = "/branches/{branchId}/branchBooks/{bookId}", method = RequestMethod.PUT, consumes="application/json")
//	public void updateBookCopies(@PathVariable Integer branchId,
//			@PathVariable Integer bookId, @RequestBody Integer noOfCopies)
//			throws SQLException {
//		Branch br = new Branch();
//		br.setBranchId(branchId);
//		HashMap<Integer,Integer> hM = new HashMap<Integer,Integer>();
//		hM.put(bookId, noOfCopies);
//		br.setBookCopies(hM);
//		brdao.updateBranchCopies(br, bookId);
//	}

}
