CREATE TABLE LottoHistory(
	round INT PRIMARY KEY
	, dat DATE
	, chucheomgi INT
	, num1_ord INT NOT NULL
	, num2_ord INT NOT NULL
	, num3_ord INT NOT NULL
	, num4_ord INT NOT NULL
	, num5_ord INT NOT NULL
	, num6_ord INT NOT NULL
	, num7 INT NOT NULL
	, nums INT ARRAY[6]
);