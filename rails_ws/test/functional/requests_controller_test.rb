require 'test_helper'

class RequestsControllerTest < ActionController::TestCase
  setup do
    @request = requests(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:requests)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create request" do
    assert_difference('Request.count') do
      post :create, request: { error: @request.error, intent_program: @request.intent_program, num_errors: @request.num_errors, num_phones: @request.num_phones, output: @request.output, requester_device_ident: @request.requester_device_ident, status: @request.status }
    end

    assert_redirected_to request_path(assigns(:request))
  end

  test "should show request" do
    get :show, id: @request
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @request
    assert_response :success
  end

  test "should update request" do
    put :update, id: @request, request: { error: @request.error, intent_program: @request.intent_program, num_errors: @request.num_errors, num_phones: @request.num_phones, output: @request.output, requester_device_ident: @request.requester_device_ident, status: @request.status }
    assert_redirected_to request_path(assigns(:request))
  end

  test "should destroy request" do
    assert_difference('Request.count', -1) do
      delete :destroy, id: @request
    end

    assert_redirected_to requests_path
  end
end
