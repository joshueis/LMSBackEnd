var lmsApp = angular.module('lmsApp', []);

lmsApp.controller('libController', function($scope, $http) {

	lc = this;
	lc.libBranches;// list of branches
	lc.books;// list of books in branch
	lc.sBranch;// selected branch
	lc.sBook;// selected branch book

	// x-editable code
	lc.xEd = function() {
		$('#brName').editable({
			type : 'text',
			value : lc.sBranch.branchName,
			placement : 'right',
			validate : function(value) {
				if ($.trim(value) == '') {
					return 'This field is required';
				} else if ($.trim(value).length < 3) {
					return 'Name must be at least 3 characters';
				}
			},
			pk : lc.sBranch.branchId,
			url : 'editBranchName'
		});
		$('#brAddr').editable({
			type : 'text',
			value : lc.sBranch.branchAddress,
			placement : 'right',
			validate : function(value) {
				if ($.trim(value) == '') {
					return 'This field is required';
				} else if ($.trim(value).length < 10) {
					return 'Address must be at least 10 characters';
				}
			},
			pk : lc.sBranch.branchId,
			url : 'editBranchAddress'
		});
	}

	lc.getLibBranches = function() {
		lc.libBranches = [];
		$http({
			method : "get",
			url : "getLibBranches",
		}).then(function(success) {
			lc.libBranches = success.data;
		});
	}

	lc.getBooks = function() {
		lc.books = [];
		$http({
			method : "get",
			url : "branches/" + lc.sBranch.branchId + "/branchBooks",
		}).then(function(success) {
			var len = success.data.length;
			for (var i = 0; i < len; i++) {
				var temp = {};
				temp.title = success.data[i].title;
				temp.bookId = success.data[i].bookId;
				temp.branchId = success.data[i].branchId;
				temp.noOfCopies = success.data[i].noOfCopies;
				temp.minVal = success.data[i].noOfCopies;
				lc.books.push(temp);
			}
			lc.sBook = lc.books[0];
		});
	}

	lc.modCopies = function(op) {
		if (op == "plus") {
			$('#mButt').attr('disabled', false);
			lc.sBook.noOfCopies++;
		} else if (op == "minus") {
			if ($('#copies').val() - 1 == $('#copies').attr('min')) {
				lc.sBook.noOfCopies--;
				$('#mButt').attr('disabled', true);
			} else
				lc.sBook.noOfCopies--;
		} else {
			$('#mButt').attr('disabled', true);
			lc.sBook.minVal = lc.sBook.noOfCopies;
		}

		// post value to server
		$http({
			method : "post",
			url : "modBookCopies",
			data : {
				branchId : lc.sBranch.branchId,
				bookId : lc.sBook.bookId,
				noOfCopies : lc.sBook.noOfCopies
			}
		});
	}

});

lmsApp.controller('borController', function($scope, $http) {
	bc = this;

	bc.borrower;// borrower card number
	bc.loans;// current loans in account
	bc.libBranches;// list of branches
	bc.books;// books in branch
	bc.sBranch;// selected checkout branch
	bc.sBook;// selected checkout book

	bc.valCardNo = function() {
		var input = document.getElementById("cardNo");
		if (input.value == "")
			input.setCustomValidity("Invalid Card Number");
		else if (input.checkValidity()) {
			$http({
				method : "post",
				url : "valCardNo",
				data : {
					cardNo : bc.borrower.cardNo
				},
			}).then(function(success) {
				if (success.data == null)
					input.setCustomValidity("Invalid Card Number");
				else {
					bc.borrower.name = success.data.name;
					bc.currentTab = 2.1;
				}
			});
		}
	}

	bc.setValid = function() {
		var input = document.getElementById("cardNo");
		input.setCustomValidity("");
	}

	bc.getLoans = function() {
		bc.loans = [];
		$http({
			method : "post",
			url : "getLoans",
			data : {
				cardNo : bc.borrower.cardNo
			}
		}).then(function(success) {
			var len = success.data.length;
			for (var i = 0; i < len; i++) {
				var temp = {};
				temp.book = success.data[i].book;
				temp.branch = success.data[i].branch;
				temp.dateOut = success.data[i].dateOut;
				temp.dueDate = success.data[i].dueDate;
				bc.loans.push(temp);
			}
		});
	}

	bc.bookReturn = function(idx) {
		$http({
			method : "post",
			url : "returnBook",
			data : {
				cardNo : bc.borrower.cardNo,
				branchId : bc.loans[idx].branch.branchId,
				bookId : bc.loans[idx].book.bookId,
				dateOut : bc.loans[idx].dateOut
			}
		}).then(function(success) {
			bc.loans.splice(idx, 1);
		});
	}

	bc.getLibBranches = function() {
		bc.libBranches = [];
		$http({
			method : "post",
			url : "getLibBranches",
		}).then(function(success) {
			var len = success.data.length;
			for (var i = 0; i < len; i++) {
				var temp = {};
				temp.branchName = success.data[i].branchName;
				temp.branchAddress = success.data[i].branchAddress;
				temp.branchId = success.data[i].branchId;
				bc.libBranches.push(temp);
			}
		});
	}

	bc.getBooks = function() {
		bc.books = [];
		$http({
			method : "post",
			url : "getAvBooks",
			data : {
				branchId : bc.sBranch.branchId
			},
		}).then(function(success) {
			var len = success.data.length;
			for (var i = 0; i < len; i++) {
				var temp = {};
				temp.title = success.data[i].title;
				temp.bookId = success.data[i].bookId;
				temp.branchId = success.data[i].branchId;
				temp.noOfCopies = success.data[i].noOfCopies;
				temp.minVal = success.data[i].noOfCopies;
				bc.books.push(temp);
			}
		});
	}

	bc.bookCheckout = function() {
		$http({
			method : "post",
			url : "bookCheckout",
			data : {
				branchId : bc.sBranch.branchId,
				bookId : bc.sBook.bookId,
				cardNo : bc.borrower.cardNo,
			}
		});
	}

});

lmsApp.controller('adminController', function($scope, $http) {
	ac = this;

	ac.bookElements = bookElements;
	ac.authorElements = authorElements;
	ac.publisherElements = publisherElements;
	ac.branchElements = branchElements;
	ac.borrowerElements = borrowerElements;

	ac.authors;// authors in DB
	ac.genres;// genres in DB
	ac.publishers;// publishers in DB
	ac.libBranches;// library branches in DB
	ac.loans;// loans in DB
	ac.borrowers;// borrowers in DB
	ac.bookTitle;// title of book to add
	ac.sAuthor;// selected author
	ac.sGenre;// selected genre
	ac.sPublisher;// selected publisher
	ac.books;// books in DB
	ac.sBook = {};// selected book
	ac.chunks;// chunks of books
	ac.pgIdx;// page index
	ac.sLoan;// selected loan
	ac.sLibBranch;// selected branch
	ac.sBorrower;// selected borrower
	ac.sBooks;// multiselect values for book
	ac.sAuthors;// multiselect values for author
	ac.sGenres;// multiselect values for genre
	ac.sBooks;// multiselect values for book

	ac.addBook = function() {
		$("#bookTitle").val("");
		$('#addModal').modal('hide');
		var authorIds;
		var genreIds;
		var publisherId;
		var authorCount;
		var genreCount;
		if (ac.sAuthors == null) {
			authorIds = [];
			authorCount = 0;
		} else {
			authorIds = ac.sAuthors;
			authorCount = authorIds.length
		}
		if (ac.sGenres == null) {
			genreIds = [];
			genreCount = 0;
		} else {
			genreIds = ac.sGenres;
			genreCount = genreIds.length
		}
		if (ac.sPublisher == null)
			publisherId = "";
		else
			publisherId = ac.sPublisher.publisherId;
		$http({
			method : "post",
			url : "addBook",
			data : {
				title : ac.bookTitle,
				authorCount : authorCount,
				authorIds : authorIds,
				genreCount : genreCount,
				genreIds : genreIds,
				publisherId : publisherId
			}
		});

	}

	ac.addAuthor = function() {
		$("#authorName").val("");
		$('#2addModal').modal('hide');
		var bookIds;
		var bookCount;
		if (ac.sBooks == null) {
			bookIds = [];
			bookCount = 0;
		} else {
			bookIds = ac.sBooks;
			bookCount = bookIds.length;
		}

		$http({
			method : "post",
			url : "addAuthor",
			data : {
				authorName : ac.authorName,
				bookCount : bookCount,
				bookIds : bookIds
			}
		});
	}

	ac.getAuthors = function() {
		ac.authors = [];
		$http({
			method : "post",
			url : "getAuthors",
		}).then(function(success) {
			var len = success.data.length;
			for (var i = 0; i < len; i++) {
				var temp = {};
				temp.authorName = success.data[i].authorName;
				temp.authorId = success.data[i].authorId;
				temp.books = success.data[i].books;
				ac.authors.push(temp);
			}
			ac.getGenres();
		});
	}

	ac.getGenres = function() {
		ac.genres = [];
		$http({
			method : "post",
			url : "getGenres",
		}).then(function(success) {
			var len = success.data.length;
			for (var i = 0; i < len; i++) {
				var temp = {};
				temp.genreName = success.data[i].genreName;
				temp.genreId = success.data[i].genreId;
				ac.genres.push(temp);
			}
			ac.getPublishers();
		});
	}

	ac.getPublishers = function() {
		ac.publishers = [];
		$http({
			method : "post",
			url : "getPublishers",
		}).then(function(success) {
			var len = success.data.length;
			for (var i = 0; i < len; i++) {
				var temp = {};
				temp.publisherName = success.data[i].publisherName;
				temp.publisherId = success.data[i].publisherId;
				temp.publisherAddress = success.data[i].publisherAddress;
				temp.publisherPhone = success.data[i].publisherPhone;
				ac.publishers.push(temp);
			}
			ac.getBooks();
		});
	}

	ac.getBooks = function(chunk) {
		ac.books = [];
		$http({
			method : "post",
			url : "getBooks",
		}).then(function(success) {
			var len = success.data.length;
			for (var i = 0; i < len; i++) {
				var temp = {};
				temp.title = success.data[i].title;
				temp.bookId = success.data[i].bookId;
				temp.authors = success.data[i].authors;
				temp.genres = success.data[i].genres;
				temp.publisher = success.data[i].publisher;
				ac.books.push(temp);
			}
			if (chunk)
				ac.chunk();
		});
	}

	ac.getLibBranches = function() {
		ac.libBranches = [];
		$http({
			method : "post",
			url : "getLibBranches",
		}).then(function(success) {
			var len = success.data.length;
			for (var i = 0; i < len; i++) {
				var temp = {};
				temp.branchName = success.data[i].branchName;
				temp.branchAddress = success.data[i].branchAddress;
				temp.branchId = success.data[i].branchId;
				ac.libBranches.push(temp);
			}
		});
	}

	ac.getBorrowers = function() {
		ac.borrowers = [];
		$http({
			method : "post",
			url : "getBorrowers",
		}).then(function(success) {
			var len = success.data.length;
			for (var i = 0; i < len; i++) {
				var temp = {};
				temp.name = success.data[i].name;
				temp.address = success.data[i].address;
				temp.phone = success.data[i].phone;
				temp.cardNo = success.data[i].cardNo;
				ac.borrowers.push(temp);
			}
		});
	}

	ac.delBook = function(idx) {
		$http({
			method : "post",
			url : "delBook",
			data : {
				bookId : ac.sBook.bookId
			}
		});
		$('#delModal').modal('hide');
	}

	ac.delAuthor = function(idx) {
		$http({
			method : "post",
			url : "delAuthor",
			data : {
				authorId : ac.sAuthor.authorId
			}
		});
		$('#2delModal').modal('hide');
	}

	ac.searchBooks = function() {
		ac.books = [];
		$http({
			method : "post",
			url : "searchBooks",
			data : {
				title : ac.searchVal
			}
		}).then(function(success) {
			var len = success.data.length;
			for (var i = 0; i < len; i++) {
				var temp = {};
				temp.title = success.data[i].title;
				temp.bookId = success.data[i].bookId;
				temp.authors = success.data[i].authors;
				temp.genres = success.data[i].genres;
				temp.publisher = success.data[i].publisher;
				ac.books.push(temp);
			}
			ac.chunk();
		});
	}

	ac.chunk = function() {
		ac.chunks = ac.chunkArr(ac.books, 3);
		ac.pgIdx = 0;
	}

	ac.chunkArr = function(arr, groupsize) {
		var sets = [];
		var chunks = arr.length / groupsize;
		for (var i = 0, j = 0; i < chunks; i++, j += groupsize) {
			sets[i] = arr.slice(j, j + groupsize);
		}
		return sets;
	}

	ac.getLoans = function() {
		ac.loans = [];
		$http({
			method : "post",
			url : "getAllLoans",
		}).then(function(success) {
			var len = success.data.length;
			for (var i = 0; i < len; i++) {
				if (success.data[i].dateIn == null) {
					var temp = {};
					temp.borrower = success.data[i].borrower;
					temp.branch = success.data[i].branch;
					temp.book = success.data[i].book;
					temp.dueDate = success.data[i].dueDate;
					temp.dateOut = success.data[i].dateOut;
					ac.loans.push(temp);
				}
			}
		});
	}

	ac.addPublisher = function() {
		$("#publisherName").val("");
		$("#publisherAddress").val("");
		$("#publisherPhone").val("");
		$('#3addModal').modal('hide');
		$http({
			method : "post",
			url : "addPublisher",
			data : {
				publisherName : ac.publisherElements.publisherName.value,
				publisherAddress : ac.publisherElements.publisherAddress.value,
				publisherPhone : ac.publisherElements.publisherPhone.value
			}
		});
	}

	ac.delPublisher = function() {
		$http({
			method : "post",
			url : "delPublisher",
			data : {
				publisherId : ac.sPublisher.publisherId
			}
		});
		$('#3delModal').modal('hide');
	}

	ac.addLibBranch = function() {
		$("#branchName").val("");
		$("#branchAddress").val("");
		$('#4addModal').modal('hide');
		$http({
			method : "post",
			url : "addLibBranch",
			data : {
				branchName : ac.branchElements.branchName.value,
				branchAddress : ac.branchElements.branchAddress.value,
			}
		});
	}

	ac.delBranch = function() {
		$http({
			method : "post",
			url : "delBranch",
			data : {
				branchId : ac.sLibBranch.branchId
			}
		});
		$('#4delModal').modal('hide');
	}

	ac.addBorrower = function() {
		$("#borrowerName").val("");
		$("#borrowerAddress").val("");
		$("#borrowerPhone").val("");
		$('#5addModal').modal('hide');
		$http({
			method : "post",
			url : "addBorrower",
			data : {
				name : ac.borrowerElements.borrowerName.value,
				address : ac.borrowerElements.borrowerAddress.value,
				phone : ac.borrowerElements.borrowerPhone.value
			}
		});
	}

	ac.delBorrower = function() {
		$http({
			method : "post",
			url : "delBorrower",
			data : {
				cardNo : ac.sBorrower.cardNo
			}
		});
		$('#5delModal').modal('hide');
	}

	// x-editable code
	ac.xEd = function(op) {
		if (op == "book") {

			$('#bTitle').editable({
				type : 'text',
				mode : 'inline',
				value : ac.sBook.title,
				placement : 'right',
				validate : function(value) {
					if ($.trim(value) == '') {
						return 'This field is required';
					} else if ($.trim(value).length < 2) {
						return 'Title must be at least 2 characters';
					}
				},
				pk : ac.sBook.bookId,
				url : 'editBookTitle'
			});

		} else if (op == "author") {

			$('#aName').editable({
				type : 'text',
				mode : 'inline',
				value : ac.sAuthor.authorName,
				placement : 'right',
				validate : function(value) {
					if ($.trim(value) == '') {
						return 'This field is required';
					} else if ($.trim(value).length < 3) {
						return 'Title must be at least 3 characters';
					}
				},
				pk : ac.sAuthor.authorId,
				url : 'editAuthorName'
			});
		} else if (op == "publisher") {

			$('#pName').editable({
				type : 'text',
				mode : 'inline',
				value : ac.sPublisher.publisherName,
				placement : 'right',
				validate : function(value) {
					if ($.trim(value) == '') {
						return 'This field is required';
					} else if ($.trim(value).length < 3) {
						return 'Name must be at least 3 characters';
					}
				},
				pk : ac.sPublisher.publisherId,
				url : 'editPublisherName'
			});

			$('#pAddress').editable({
				type : 'text',
				mode : 'inline',
				value : ac.sPublisher.publisherAddress,
				placement : 'right',
				validate : function(value) {
					if ($.trim(value) == '') {
						return 'This field is required';
					} else if ($.trim(value).length < 5) {
						return 'Address must be at least 5 characters';
					}
				},
				pk : ac.sPublisher.publisherId,
				url : 'editPublisherAddress'
			});

			$('#pPhone').editable({
				type : 'text',
				mode : 'inline',
				value : ac.sPublisher.publisherPhone,
				placement : 'right',
				validate : function(value) {
					if ($.trim(value) == '') {
						return 'This field is required';
					} else if ($.trim(value).length < 5) {
						return 'Phone must be at least 5 characters';
					}
				},
				pk : ac.sPublisher.publisherId,
				url : 'editPublisherPhone'
			});
		} else if (op == "libBranch") {
			$('#bName').editable({
				type : 'text',
				mode : 'inline',
				value : ac.sLibBranch.branchName,
				placement : 'right',
				validate : function(value) {
					if ($.trim(value) == '') {
						return 'This field is required';
					} else if ($.trim(value).length < 3) {
						return 'Name must be at least 3 characters';
					}
				},
				pk : ac.sLibBranch.branchId,
				url : 'editBranchName'
			});

			$('#bAddress').editable({
				type : 'text',
				mode : 'inline',
				value : ac.sLibBranch.branchAddress,
				placement : 'right',
				validate : function(value) {
					if ($.trim(value) == '') {
						return 'This field is required';
					} else if ($.trim(value).length < 5) {
						return 'Address must be at least 5 characters';
					}
				},
				pk : ac.sLibBranch.branchId,
				url : 'editBranchAddress'
			});
		} else if (op == "borrower") {
			$('#brName').editable({
				type : 'text',
				mode : 'inline',
				value : ac.sBorrower.name,
				placement : 'right',
				validate : function(value) {
					if ($.trim(value) == '') {
						return 'This field is required';
					} else if ($.trim(value).length < 3) {
						return 'Name must be at least 3 characters';
					}
				},
				pk : ac.sBorrower.cardNo,
				url : 'editBorrowerName'
			});

			$('#brAddress').editable({
				type : 'text',
				mode : 'inline',
				value : ac.sBorrower.address,
				placement : 'right',
				validate : function(value) {
					if ($.trim(value) == '') {
						return 'This field is required';
					} else if ($.trim(value).length < 5) {
						return 'Address must be at least 5 characters';
					}
				},
				pk : ac.sBorrower.borrowerId,
				url : 'editBorrowerAddress'
			});

			$('#brPhone').editable({
				type : 'text',
				mode : 'inline',
				value : ac.sBorrower.phone,
				placement : 'right',
				validate : function(value) {
					if ($.trim(value) == '') {
						return 'This field is required';
					} else if ($.trim(value).length < 5) {
						return 'Phone must be at least 5 characters';
					}
				},
				pk : ac.sBorrower.cardNo,
				url : 'editBorrowerPhone'
			});
		} else if (op == "loan") {
			$('#loanDD').editable({
				value : ac.sLoan.dueDate,
				mode : 'popup',
				placement : 'right',
				format : 'YYYY-MM-DD',
				combodate : {
					minYear : new Date(ac.sLoan.dateOut).getFullYear(),
					maxYear : new Date(ac.sLoan.dateOut).getFullYear() + 2
				},
				validate : function(value) {
					if ($.trim(value) == '') {
						return 'All fields are required';
					} else if (new Date(value) < new Date(ac.sLoan.dueDate)) {
						return 'Date must be greater than current Due Date';
					}
				},
				params : {
					dateOut : ac.sLoan.dateOut,
					cardNo : ac.sLoan.borrower.cardNo,
					branchId : ac.sLoan.branch.branchId,
					bookId : ac.sLoan.book.bookId
				},
				pk : '1',
				url : 'changeDD'
			});
		}

	}

	ac.xEdR = function(op) {
		if (op == "book") {
			$('#bTitle').editable('setValue', ac.sBook.title).editable(
					'option', 'pk', ac.sBook.bookId);
		} else if (op == "author") {

			$('#aName').editable('setValue', ac.sAuthor.authorName).editable(
					'option', 'pk', ac.sAuthor.authorId);

		} else if (op == "publisher") {
			$('#pName').editable('setValue', ac.sPublisher.publisherName)
					.editable('option', 'pk', ac.sPublisher.publisherId);
			$('#pAddress').editable('setValue', ac.sPublisher.publisherAddress)
					.editable('option', 'pk', ac.sPublisher.publisherId);
			$('#pPhone').editable('setValue', ac.sPublisher.publisherPhone)
					.editable('option', 'pk', ac.sPublisher.publisherId);

		} else if (op == "libBranch") {
			$('#bName').editable('setValue', ac.sLibBranch.branchName)
					.editable('option', 'pk', ac.sLibBranch.branchId);
			$('#bAddress').editable('setValue', ac.sLibBranch.branchAddress)
					.editable('option', 'pk', ac.sLibBranch.branchId);
		} else if (op == "borrower") {
			$('#brName').editable('setValue', ac.sBorrower.name).editable(
					'option', 'pk', ac.sBorrower.cardNo);
			$('#brAddress').editable('setValue', ac.sBorrower.address)
					.editable('option', 'pk', ac.sBorrower.cardNo);
			$('#brPhone').editable('setValue', ac.sBorrower.phone).editable(
					'option', 'pk', ac.sBorrower.cardNo);
		} else if (op == "loan") {
			$('#loanDD').editable('setValue', moment(ac.sLoan.dueDate))
					.editable('option', 'params', {
						dateOut : ac.sLoan.dateOut,
						cardNo : ac.sLoan.borrower.cardNo,
						branchId : ac.sLoan.branch.branchId,
						bookId : ac.sLoan.book.bookId
					}).editable('option', 'combodate', {
						minYear : new Date(ac.sLoan.dateOut).getFullYear(),
						maxYear : new Date(ac.sLoan.dateOut).getFullYear() + 2
					});
		}

	}

});
