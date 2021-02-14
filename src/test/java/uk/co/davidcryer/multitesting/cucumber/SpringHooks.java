package uk.co.davidcryer.multitesting.cucumber;

import io.cucumber.java.After;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import uk.co.davidcryer.multitesting.utils.TestKafkaConsumer;

public class SpringHooks implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public TestKafkaConsumer obtainKafkaHelper() {
        return beanFactory.getBean(TestKafkaConsumer.class);
    }

    @After(value = "@kafka", order = 100)
    public void rollBackTransaction() {
        obtainKafkaHelper().clear();
    }
}
