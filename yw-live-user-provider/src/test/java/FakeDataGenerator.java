import com.github.javafaker.Faker;

import java.util.Locale;

public class FakeDataGenerator {
    public static void main(String[] args) {
        // 创建中文数据生成器
        Faker faker = new Faker(Locale.CHINA);

        // 生成人名和地址
        for (int i = 0; i < 5; i++) {
            String name = faker.name().fullName();       // 姓名
            String address = faker.address().fullAddress(); // 地址
            String avatar = faker.avatar().image();
            String funnyName = faker.funnyName().name();
            System.out.println("姓名: " + name);
            System.out.println("地址: " + address);
            System.out.println("avatar: " + avatar);
            System.out.println("funnyName: " + funnyName);
            System.out.println("-----------------------------");
        }
    }
}