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
	
	@Test
	void testDefaultFindByName() {
		// デフォルトメソッドで直接T取得
		assertEquals(target.findByName("Suzuki").getId(), 1L);
		assertNull(target.findByName("Nanasi"));
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

		// Splate経路でも確認
		assertEquals(target.sampleCount(null, null), 4);
	}

	@Test
	void testCount() {
		// count
		assertEquals(target.sampleCount(1700, 2000), 3);
	}

	@Test
	void testQueryForListAll() {
		// 条件なしで全件
		assertEquals(target.queryForList(null, null).size(), 3);
	}

	@Test
	void testQueryForListNoMatch() {
		// 該当なしで空List
		assertEquals(target.queryForList(3000, 4000).size(), 0);
	}

	@Test
	void testCountAll() {
		// 条件なし
		assertEquals(target.sampleCount(null, null), 3);
	}

	@Test
	void testCountLeftOnly() {
		// 上限のみ
		assertEquals(target.sampleCount(null, 1999), 2);
	}

	@Test
	void testCountRightOnly() {
		// 下限のみ
		assertEquals(target.sampleCount(2000, null), 1);
	}

	@Test
	void testCountNoMatch() {
		// 該当なし
		assertEquals(target.sampleCount(3000, 4000), 0);
	}

	// ↓↓@Splate 単一JavaBean引数↓↓

	@Test
	void testQueryForListByCondition() {
		// salaryMin=1700, salaryMax=2000 で3件
		assertEquals(target.queryForListByCondition(condition(1700, 2000)).size(), 3);
		// salaryMin=1701, salaryMax=1999 で1件
		assertEquals(target.queryForListByCondition(condition(1701, 1999)).size(), 1);
		// salaryMin=null, salaryMax=null で全3件
		assertEquals(target.queryForListByCondition(condition(null, null)).size(), 3);
		// 該当なし条件で空List
		assertEquals(target.queryForListByCondition(condition(3000, 4000)).size(), 0);
	}

	@Test
	void testSampleCountByCondition() {
		// salaryMin=null, salaryMax=null で3件
		assertEquals(target.sampleCountByCondition(condition(null, null)), 3);
		// salaryMin=null, salaryMax=1999 で2件
		assertEquals(target.sampleCountByCondition(condition(null, 1999)), 2);
		// salaryMin=2000, salaryMax=null で1件
		assertEquals(target.sampleCountByCondition(condition(2000, null)), 1);
		// 該当なし条件で0件
		assertEquals(target.sampleCountByCondition(condition(3000, 4000)), 0);
	}

	@Test
	void testInsertByCommand() {
		// JavaBean引数によるINSERT
		EmployeeCreateCommand command = new EmployeeCreateCommand();
		command.setId(4L);
		command.setName("Test");
		command.setSalary(3000);

		int count = target.sampleInsertByCommand(command);
		assertEquals(count, 1);

		Optional<Employee> ret = target.findById(4L);
		assertTrue(ret.isPresent());
		assertEquals(ret.get().getId(), 4L);
		assertEquals(ret.get().getName(), "Test");
		assertEquals(ret.get().getSalary(), 3000);

		// Splate経路でも確認
		assertEquals(target.sampleCount(null, null), 4);
	}

	@Test
	void testQueryForListByConditionNull() {
		// nullの単一JavaBean引数はIllegalArgumentException
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> target.queryForListByCondition(null));
		String msg = ex.getMessage().toLowerCase();
		assertTrue(msg.contains("javabean") || msg.contains("bean"),
				"Exception message should mention JavaBean/bean: " + ex.getMessage());
		assertTrue(msg.contains("null"),
				"Exception message should mention null: " + ex.getMessage());
	}

	private static EmployeeSearchCondition condition(Integer salaryMin, Integer salaryMax) {
		EmployeeSearchCondition c = new EmployeeSearchCondition();
		c.setSalaryMin(salaryMin);
		c.setSalaryMax(salaryMax);
		return c;
	}
}
