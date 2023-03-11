package com.github.whitenoise0000.springdatajdbcsplate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
class EmployeeRepositoryTest {

	@Autowired
	private EmployeeRepository target;

	@Test
	void testFindById() {
		// デフォルトメソッドが引き続き使えること
		assertEquals(target.findById(1L).get().getName(), "Suzuki");
	}

	@Test
	void testCount() {
		// デフォルトメソッドが引き続き使えること
		assertEquals(target.count(), 3);
	}

	@Test
	void queryForList() {
		// List型戻り値
		assertEquals(target.queryForList(1700, 2000).size(), 3);
		assertEquals(target.queryForList(1701, 1999).size(), 1);
		assertEquals(target.queryForList(null, 1999).size(), 2);
		assertEquals(target.queryForList(2000, null).size(), 1);
	}
	
	// TODO List以外のテストケース実装
	@Test
	void queryForStream() {
		// Stream戻り値
		assertEquals(target.queryForStream(1700, 2000).count(), 3);
		assertEquals(target.queryForList(1701, 1999).get(0).getName(), "Suzuki");
	}
	
	@Test
	void querySingle() {
		assertEquals(target.querySingle(2L).get().getName(), "Sato");
	}
}
