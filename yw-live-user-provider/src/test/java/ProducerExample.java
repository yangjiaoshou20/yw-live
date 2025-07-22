import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class ProducerExample {
    public static void main(String[] args) throws Exception {
        // 创建生产者实例，并设置生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("TestProducer");
        // 设置 Name Server 地址，此处为示例，实际使用时请替换为真实的 Name Server 地址
        producer.setNamesrvAddr("192.168.211.132:9876");
        producer.start();

        try {
            // 创建消息实例，指定 topic、Tag和消息体
            Message msg = new Message("TestTopic-01", "TagA", ("Hello RocketMQ").getBytes());
            // 发送消息并获取发送结果
            for (int i = 0; i < 10; i++) {
                SendResult sendResult = producer.send(msg);
                System.out.println("Message sent: " + new String(msg.getBody()));
                System.out.println("Send result: " + sendResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Message sending failed.");
        } finally {
            // 关闭生产者
            producer.shutdown();
        }
    }
}