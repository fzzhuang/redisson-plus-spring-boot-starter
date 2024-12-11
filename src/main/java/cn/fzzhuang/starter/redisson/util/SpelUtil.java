package cn.fzzhuang.starter.redisson.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * EL表达式工具类
 *
 * @author Fu.zhizhuang
 */
public class SpelUtil {

    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 解析El表达式
     *
     * @param method 方法
     * @param args   参数
     * @param spEl   el表达式
     * @return 解析后信息
     */
    public static String parseEl(Method method, Object[] args, String spEl) {
        //解析参数名
        String[] params = Optional.ofNullable(parameterNameDiscoverer.getParameterNames(method)).orElse(new String[]{});
        //el解析需要的上下文对象
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < params.length; i++) {
            //所有参数都作为原材料扔进去
            context.setVariable(params[i], args[i]);
        }
        if (StringUtils.isBlank(spEl)) return "";
        if (!spEl.startsWith("#")) return spEl;
        Expression expression = parser.parseExpression(spEl);
        return expression.getValue(context, String.class);
    }

    /***
     * 获取方法key
     * @param method 方法
     * @return keys
     */
    public static String getMethodKey(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }
}
