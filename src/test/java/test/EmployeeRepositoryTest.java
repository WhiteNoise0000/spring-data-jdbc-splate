package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

@DataJdbcTest
class EmployeeRepositoryTest {

	@Autowired
	private EmployeeRepository target;

	@Test
	void testDefaultFindById() {
		// デフォルトメソッドが引き続き使えること
		assertEquals(target.findById(1L).get().getName(), "Suzuki");
	}

	@Test
	void testDefaultCount() {
		// デフォルトメソッドが引き続き使えること
		assertEquals(target.count(), 3);
	}

	// ↓↓splateテスト↓↓

	@Test
	void testQueryForList() {
		// List型戻り値
		assertEquals(target.queryForList(1700, 2000).size(), 3);
		assertEquals(target.queryForList(1701, 1999).size(), 1);
		assertEquals(target.queryForList(null, 1999).size(), 2);
		assertEquals(target.queryForList(2000, null).size(), 1);
	}

	@Test
	void testQueryForStream() {
		// Stream戻り値
		assertEquals(target.queryForStream(1700, 2000).count(), 3);
		assertEquals(target.queryForList(1701, 1999).get(0).getName(), "Suzuki");
	}

	@Test
	void testQuerySingle() {
		// 1件取得(Optionalあり)
		assertEquals(target.querySingle(2L).get().getName(), "Sato");
	}

	@Test
	void testQuerySingle2() {
		// 1件取得(Optionalなし)
		assertEquals(target.querySingle2(2L).getName(), "Sato");
	}

	@Test
	void testInsert() {
		// INSERT
		int count = target.sampleInsert(4L, "Test", 3000);
		assertEquals(count, 1);

		Optional<Employee> ret = target.findById(4L);
		assertTrue(ret.isPresent());
		assertEquals(ret.get().getId(), 4L);
		assertEquals(ret.get().getName(), "Test");
		assertEquals(ret.get().getSalary(), 3000);
	}

	@Test
	void testCount() {
		// count
		assertEquals(target.sampleCount(1700, 2000), 3);
	}

	// TODO テストケース充実
}
