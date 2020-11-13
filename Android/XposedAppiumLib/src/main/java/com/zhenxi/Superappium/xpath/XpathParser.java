package com.zhenxi.Superappium.xpath;

import android.util.LruCache;

import com.zhenxi.Superappium.xpath.exception.XpathSyntaxErrorException;
import com.zhenxi.Superappium.xpath.model.XpathEvaluator;
import com.zhenxi.Superappium.xpath.parser.TokenQueue;
import com.zhenxi.Superappium.xpath.parser.XpathStateMachine;

public class XpathParser {
    private String xpathStr;

    public String getXpathStr() {
        return xpathStr;
    }

    private TokenQueue tokenQueue;

    private static LruCache<String, XpathEvaluator> cache = new LruCache<>(128);

    public XpathEvaluator parse() throws XpathSyntaxErrorException {
        XpathStateMachine xpathStateMachine = new XpathStateMachine(tokenQueue);
        while (xpathStateMachine.getState() != XpathStateMachine.BuilderState.END) {
            xpathStateMachine.getState().parse(xpathStateMachine);
        }
        return xpathStateMachine.getEvaluator();
    }

    /**
     * no error������÷���ȷ֪��xpathû���﷨����,�����������,��һ������ķ���,����������ʽȷʵ���﷨����,�������ܳ��Ƿ�״̬�쳣
     *
     * @param xpathStr xpath���ʽ
     * @return ��ģ��������xpath��ȡ��
     */
    public static XpathEvaluator compileNoError(String xpathStr) {
        try {
            return compile(xpathStr);
        } catch (XpathSyntaxErrorException e) {
            throw new IllegalStateException("parse xpath \"" + xpathStr + "\" failed", e);
        }
    }

    public XpathParser(String subXpath) {
        this.xpathStr = subXpath;
        tokenQueue = new TokenQueue(xpathStr);
    }

    public static XpathEvaluator compile(String xpathStr) throws XpathSyntaxErrorException {
        if (xpathStr == null) {
            throw new XpathSyntaxErrorException(0, "xpathStr can not be null");
        }
        XpathEvaluator xpathEvaluator = cache.get(xpathStr);
        if (xpathEvaluator == null) {
            xpathEvaluator = new XpathParser(xpathStr).parse();
            cache.put(xpathStr, xpathEvaluator);
            xpathEvaluator = cache.get(xpathStr);

        }
        return xpathEvaluator;
    }

}
