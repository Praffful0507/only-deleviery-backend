package com.prafful.springjwt.spel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.exception.DataException;
import org.springframework.asm.MethodVisitor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.CompilablePropertyAccessor;

import io.jsonwebtoken.lang.Assert;

public class PraffulAccesor implements CompilablePropertyAccessor {

	@Override
	public Class<?>[] getSpecificTargetClasses() {
		// TODO Auto-generated method stub
		return new Class<?>[] { PraffulData.class };
	}

	@Override
	public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
		if (target instanceof PraffulData pD) {
			final Map<String, Object> mapData = pD.getData();
			if (mapData.containsKey(name)) {
				return mapData.containsKey(name);
			} else if (pD.getListData() != null && !pD.getListData().isEmpty()) {
				List<Map<String, Object>> mapListData = pD.getListData();
				for (Map<String, Object> data : mapListData) {
					return data.containsKey(name);
				}
			}
		}
		throw new RuntimeException("Field :" + name + "is missing in the provided root object");
	}

	@Override
	public TypedValue read(EvaluationContext context, Object target, String name) {
		Assert.state(target instanceof PraffulData, "Target must be of type SpelData");
		PraffulData pD = (PraffulData) target;
		final Map<String, Object> map = pD.getData();
		if (map.containsKey(name)) {
			Object value = map.get(name);
			if (value instanceof BigDecimal bigValue) {
				if (bigValue.signum() == 0) {
					return new TypedValue(0.0);
				}
			}
			if (value == null && !map.containsKey(name)) {
				throw new DataException("Unable to access map data with keys: " + name, null);
			}
			return new TypedValue(value);
		} else if (!pD.getListData().isEmpty()) {
			List<Map<String, Object>> listData = pD.getListData();
			for (Map<String, Object> data : listData) {
				return new TypedValue(data.get(name));
			}
		}
		throw new DataException("The key is not present in either the map or in List-Data", null);
	}

	@Override
	public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
		return true;
	}

	@Override
	public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
		Assert.state(target instanceof PraffulData, "Target must be of type SpelData");
		PraffulData pD = (PraffulData) target;
		final Map<String, Object> map = pD.getData();
		map.put(name, newValue);

	}

	@Override
	public boolean isCompilable() {
		return true;
	}

	@Override
	public Class<?> getPropertyType() {
		return Object.class;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void generateCode(String propertyName, MethodVisitor mv, CodeFlow cf) {
		String descriptor = cf.lastDescriptor();
		if(descriptor == null || !descriptor.equals("Ljava/util/Map")) {
			if(descriptor == null) {
				cf.loadTarget(mv);
			}
			CodeFlow.insertCheckCast(mv, "Ljava/util/Map");
		}
		mv.visitLdcInsn(propertyName);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;",  true);
	}

}
